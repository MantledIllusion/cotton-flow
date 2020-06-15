package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.essentials.object.Null;
import com.mantledillusion.essentials.reflection.TypeEssentials;
import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Bus;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostDestroy;
import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;
import com.mantledillusion.metrics.trail.*;
import com.mantledillusion.metrics.trail.api.Metric;
import com.mantledillusion.metrics.trail.api.MetricAttribute;
import com.mantledillusion.vaadin.cotton.event.responsive.AfterResponsiveRefreshEvent;
import com.mantledillusion.vaadin.cotton.event.responsive.BeforeResponsiveRefreshEvent;
import com.mantledillusion.vaadin.cotton.exception.http500.Http500InternalServerErrorException;
import com.mantledillusion.vaadin.cotton.metrics.BrowserType;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;
import com.mantledillusion.vaadin.cotton.metrics.SystemEnvironmentType;
import com.mantledillusion.vaadin.cotton.viewpresenter.PrioritizedRouteAlias;
import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.BrowserWindowResizeEvent;
import com.vaadin.flow.component.page.BrowserWindowResizeListener;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.*;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

class CottonServletService extends VaadinServletService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CottonServletService.class);
	private static final String JAR = "jar";
	private static final String FILE = "file";

	static final String SID_SERVLETSERVICE = "_servletService";
	static final String PKEY_RESPONSIVE_ADAPTION_WAIT_MS = "_responsiveAdaptionWaitMs";
	static final String DEFAULT_RESPONSIVE_ADAPTION_WAIT_MS = "2000";

	private final class CottonInstantiator extends DefaultInstantiator {

		public CottonInstantiator() {
			super(CottonServletService.this);
		}

		@Override
		public I18NProvider getI18NProvider() {
			return CottonServletService.this.localizer;
		}

		@Override
		public <T extends HasElement> T createRouteTarget(Class<T> routeTargetType, NavigationEvent event) {
			T target;

			if (routeTargetType.isAnnotationPresent(Responsive.class)) {
				CottonResponsiveWrapper wrapper = CottonUI.current().exchangeInjectedView(CottonResponsiveWrapper.class);
				CottonUI.current().getPage().retrieveExtendedClientDetails(details ->
						wrapper.initialize((Class<? extends Component>) routeTargetType, details));
				target = (T) wrapper;
			} else {
				long ms = System.currentTimeMillis();
				target = CottonUI.current().exchangeInjectedView(routeTargetType);
				ms = System.currentTimeMillis() - ms;

				MetricsTrailSupport.commit(CottonMetrics.SYSTEM_INJECTION.build(ms,
						new MetricAttribute("class", routeTargetType.getName())));
			}
			return target;
		}

		@Override
		public <T> T getOrCreate(Class<T> type) {
			return CottonServletService.this.serviceInjector.instantiate(type);
		}
	}

	static class CottonResponsiveWrapper extends Div implements BrowserWindowResizeListener {

		private final Injector injector;
		private final Registration registration;
		private final ExecutorService executorService;
		private final int responsiveAdaptionWaitMs;
		private Pair<Integer, Integer> resizeDimension;
		private long resizeWaitMs;

		private Class<? extends Component> rootRouteTargetType;
		private Responsive.Alternative[] alternativeRouteTargets;
		private boolean asyncResponsiveAdaptionEnabled;
		private boolean isMobileDevice;
		private boolean isTouchDevice;


		@Construct
		private CottonResponsiveWrapper(@Inject Injector injector,
										@Resolve("${" + PKEY_RESPONSIVE_ADAPTION_WAIT_MS + ":" + DEFAULT_RESPONSIVE_ADAPTION_WAIT_MS + "}") String responsiveAdaptionWaitMs) {
			this.injector = injector;
			this.registration = CottonUI.current().getPage().addBrowserWindowResizeListener(this);
			this.executorService = Executors.newSingleThreadExecutor();
			this.responsiveAdaptionWaitMs = Math.max(0, Integer.parseInt(responsiveAdaptionWaitMs));
		}

		private <T extends Component> void initialize(Class<T> rootRouteTargetType, ExtendedClientDetails clientDetails) {
			this.rootRouteTargetType = rootRouteTargetType;
			this.alternativeRouteTargets = rootRouteTargetType.getAnnotation(Responsive.class).value();
			this.asyncResponsiveAdaptionEnabled = CottonServletService.getCurrent().ensurePushAvailable() &&
					rootRouteTargetType.isAnnotationPresent(Push.class);

			this.isMobileDevice = isMobileDevice(CottonSession.current().getBrowser());
			this.isTouchDevice = clientDetails.isTouchDevice();

			Responsive.Alternative alternative = determineViewType(clientDetails.getWindowInnerWidth(),
					clientDetails.getWindowInnerHeight());
			injectAlternative(alternative != null ? alternative.value() : this.rootRouteTargetType);
		}

		private boolean isMobileDevice(WebBrowser webBrowser) {
			return webBrowser.isWindowsPhone() || webBrowser.isAndroid() || webBrowser.isChromeOS() || webBrowser.isIPhone();
		}

		@Override
		public void browserWindowResized(BrowserWindowResizeEvent resizeEvent) {
			if (this.asyncResponsiveAdaptionEnabled) {
				boolean beginTask = false;
				synchronized (this) {
					this.resizeWaitMs = System.currentTimeMillis() + this.responsiveAdaptionWaitMs;
					if (this.resizeDimension == null) {
						this.resizeDimension = Pair.of(resizeEvent.getWidth(), resizeEvent.getHeight());
						beginTask = true;
					}
				}

				if (beginTask) {
					CottonUI cottonUI = CottonUI.current();
					this.executorService.execute(() -> {
						try {
							while (true) {
								long waitMs;
								synchronized (CottonResponsiveWrapper.this) {
									waitMs = this.resizeWaitMs - System.currentTimeMillis();
								}
								if (waitMs <= 0) {
									break;
								}
								Thread.sleep(waitMs);
							}
						} catch (InterruptedException e) {
							LOGGER.warn("Unable to wait for responsive adaption; adapting immediately.", e);
						}

						synchronized (CottonResponsiveWrapper.this) {
							Pair<Integer, Integer> resizeDimension = this.resizeDimension;
							this.resizeDimension = null;
							cottonUI.access(() -> adaptIfRequired(resizeDimension.getLeft(),
									resizeDimension.getRight(), Responsive.Alternative.AdaptionMode.PERFORM));
						}
					});
				}
			} else {
				adaptIfRequired(resizeEvent.getWidth(), resizeEvent.getHeight(), Responsive.Alternative.AdaptionMode.PERFORM);
			}
		}

		void adaptIfRequired(int width, int height, Responsive.Alternative.AdaptionMode adaptionMode) {
			Responsive.Alternative alternative = determineViewType(width, height);
			Class<? extends Component> targetViewType;
			if (alternative != null) {
				targetViewType = alternative.value();
				adaptionMode = Responsive.Alternative.AdaptionMode.combine(adaptionMode, alternative.automaticAdaptionMode());
			} else {
				targetViewType = this.rootRouteTargetType;
			}

			if (getChildren().noneMatch(child -> child.getClass() == targetViewType)) {
				BeforeResponsiveRefreshEvent event = new BeforeResponsiveRefreshEvent(CottonUI.current(), adaptionMode);
				CottonUI.getCurrent().getNavigationListeners(CottonUI.BeforeResponsiveRefreshListener.class).
						forEach(listener -> listener.beforeRefresh(event));

				if (event.isAccepted()) {
					getChildren().forEach(this.injector::destroy);
					injectAlternative(targetViewType);

					AfterResponsiveRefreshEvent afterEvent = new AfterResponsiveRefreshEvent(CottonUI.current());
					CottonUI.getCurrent().getNavigationListeners(CottonUI.AfterResponsiveRefreshListener.class).
							forEach(listener -> listener.afterRefresh(afterEvent));
				}
			}
		}

		private Responsive.Alternative determineViewType(int width, int height) {
			Map<Class<? extends Component>, Responsive.Alternative> matches = Arrays.stream(this.alternativeRouteTargets).
					filter(alternative -> matchesClientEnvironment(alternative, width, height)).
					collect(Collectors.toMap(Responsive.Alternative::value, alternative -> alternative));

			if (matches.size() == 1) {
				return matches.values().iterator().next();
			} else if (!matches.isEmpty()) {
				LOGGER.warn("The @" + Responsive.class.getSimpleName()+ " class " +
						this.rootRouteTargetType.getSimpleName() + " specifies " + this.alternativeRouteTargets.length +
						" alternatives of which the configuration of " + matches.size() + " (" +
						StringUtils.join(matches.keySet().stream().map(Class::getSimpleName), ", ") +
						") match to the client's environment (isTouchDevice=" + this.isTouchDevice + ", width=" +
						width + ", height=" + height + "); cannot decide which alternative to choose.");
			}
			return null;
		}

		private boolean matchesClientEnvironment(Responsive.Alternative alternative, int width, int height) {
			if (this.isMobileDevice && alternative.isMobileDevice() == Responsive.Alternative.DeviceHint.FALSE ||
					!this.isMobileDevice && alternative.isMobileDevice() == Responsive.Alternative.DeviceHint.TRUE) {
				return false;
			} else if (this.isTouchDevice && alternative.isTouchDevice() == Responsive.Alternative.DeviceHint.FALSE ||
					!this.isTouchDevice && alternative.isTouchDevice() == Responsive.Alternative.DeviceHint.TRUE) {
				return false;
			}

			switch (alternative.mode()) {
				case ABSOLUTE:
					return width >= alternative.fromX() && width <= alternative.toX() &&
							height >= alternative.fromY() && height <= alternative.toY();
				case RATIO:
					double clientRatio = width / (double) height;
					double alternativeFromRatio = Math.min(alternative.fromX() / (double) alternative.fromY(),
							alternative.toX() / (double) alternative.toY());
					double alternativeToRatio = Math.max(alternative.fromX() / (double) alternative.fromY(),
							alternative.toX() / (double) alternative.toY());
					return clientRatio >= alternativeFromRatio && clientRatio <= alternativeToRatio;
				default:
					throw new Http500InternalServerErrorException("Handling of alternative mode " +
							alternative.mode().name() + " not implemented");
			}
		}

		private void injectAlternative(Class<? extends Component> targetViewType) {
			removeAll();

			long ms = System.currentTimeMillis();
			add(this.injector.instantiate(targetViewType));
			ms = System.currentTimeMillis() - ms;

			Metric metric = CottonMetrics.SYSTEM_INJECTION.build(ms,
					new MetricAttribute("class", targetViewType.getName()));
			if (targetViewType != this.rootRouteTargetType) {
				metric.getAttributes().add(new MetricAttribute("alternativeTo", this.rootRouteTargetType.getName()));
			}
			MetricsTrailSupport.commit(metric);
		}

		@PostDestroy
		private void destroy() {
			this.registration.remove();
		}
	}

	private final Injector serviceInjector;
	private final Localizer localizer;
	private final AccessHandler accessHandler;
	private final String applicationInitializerClass;
	private final String applicationBasePackage;
	private final boolean automaticRouteDiscovery;

	CottonServletService(@Inject @Qualifier(CottonServlet.SID_SERVLET) VaadinServlet servlet,
						 @Inject @Qualifier(CottonServlet.SID_DEPLOYMENTCONFIG) DeploymentConfiguration deploymentConfiguration,
						 @Inject @Qualifier(Localizer.SID_LOCALIZER) Localizer localizer,
						 @Inject @Qualifier(AccessHandler.SID_NAVIGATION_HANDLER) AccessHandler accessHandler,
						 @Inject Injector serviceInjector,
						 @Resolve("${" + CottonServlet.PID_INITIALIZERCLASS + "}") String applicationInitializerClass,
						 @Resolve("${" + CottonServlet.PID_BASEPACKAGE + "}") String applicationBasePackage,
						 @Resolve("${" + CottonEnvironment.PKEY_AUTOMATIC_ROUTE_DISCOVERY + ":false}") @Matches("(true)|(false)") String automaticRouteDiscovery) {
		super(servlet, deploymentConfiguration);
		this.serviceInjector = serviceInjector;
		this.localizer = localizer;
		this.accessHandler = accessHandler;
		this.applicationInitializerClass = applicationInitializerClass;
		this.applicationBasePackage = applicationBasePackage;
		this.automaticRouteDiscovery = Boolean.parseBoolean(automaticRouteDiscovery);
	}

	@Override
	protected Optional<Instantiator> loadInstantiators() {
		return Optional.of(new CottonInstantiator());
	}

	@Override
	public void init() throws ServiceException {
		super.init();

		// DISCOVER ROUTES
		if (this.automaticRouteDiscovery) {
			Class<?> applicationInitializerClass = load(this.applicationInitializerClass);
			String applicationBasePath = this.applicationBasePackage.replace('.', '/');
			try {
				URL url = applicationInitializerClass.getProtectionDomain().getCodeSource().getLocation();
				if (FILE.equals(url.getProtocol())) {
					File file = new File(url.toURI());
					if (file.isDirectory()) {
						getClasses(applicationBasePath).parallelStream().forEach(this::registerIfRoute);
					} else if (file.getName().toLowerCase().endsWith(JAR)) {
						readJar(new JarFile(file), applicationBasePath);
					}
				} else if(JAR.equals(url.getProtocol())) {
					readJar(((JarFile) url.getContent()), applicationBasePath);
				}
			} catch (Exception e) {
				throw new ServiceException("Automatic @" + Route.class.getSimpleName() + " detection failed", e);
			}
		}

		Set<String> forwardedPaths = new HashSet<>();
		for (RouteData routeData: getRouter().getRegistry().getRegisteredRoutes()) {
			List<PrioritizedRouteAlias> routeAliases = new ArrayList<>();
			TypeEssentials.getSuperClassesAnnotatedWith(routeData.getNavigationTarget(),
					PrioritizedRouteAlias.PrioritizedRouteAliases.class).stream().
					map(c -> c.getAnnotation(PrioritizedRouteAlias.PrioritizedRouteAliases.class)).
					flatMap(c -> Arrays.stream(c.value())).
					forEach(routeAliases::add);
			TypeEssentials.getSuperClassesAnnotatedWith(routeData.getNavigationTarget(),
					PrioritizedRouteAlias.class).stream().
					map(c -> c.getAnnotation(PrioritizedRouteAlias.class)).
					forEach(routeAliases::add);
			for (PrioritizedRouteAlias routeAlias: routeAliases) {
				this.accessHandler.register(routeAlias.value(), routeData.getNavigationTarget(), routeAlias.priority());
				forwardedPaths.add(routeAlias.value());
			}
		}
		forwardedPaths.forEach(path -> getRouter().getRegistry().
				setRoute(path, AccessHandler.ForwardingView.class, Collections.emptyList()));

		// REGISTER ON NEW TRAILS
		this.serviceInjector.aggregate(MetricsTrailConsumer.class).forEach(consumer ->
				MetricsTrailSupport.addPersistentHook(consumer, MetricsTrailListener.ReferenceMode.WEAK));
	}

	// #################################################################################################################
	// ############################################### DISCOVER ROUTES #################################################
	// #################################################################################################################

	private void readJar(JarFile jarFile, String applicationBasePath) {
		Collections.list(jarFile.entries()).parallelStream()
				.filter(e -> e.getName().startsWith(applicationBasePath) && e.getName().endsWith(".class"))
				.forEach(e -> registerIfRoute(load(e.getName().replace('/', '.').replace(".class", ""))));
	}

	private <C extends Component & HasUrlParameter<String>> void registerIfRoute(Class<?> clazz) {
		if (Component.class.isAssignableFrom(clazz) && (clazz.isAnnotationPresent(Route.class) || clazz.isAnnotationPresent(RouteAlias.class))) {
			Class<? extends Component> routeTarget = (Class<? extends Component>) clazz;
			RouteConfiguration router = RouteConfiguration.forRegistry(getRouter().getRegistry());
			router.setAnnotatedRoute(routeTarget);
			if (HasUrlParameter.class.isAssignableFrom(clazz)) {
				LOGGER.debug("Routing '" + clazz.getSimpleName() + "' to '" + router.getUrl((Class<C>) routeTarget, "{parameter}") + "'");
			} else {
				LOGGER.debug("Routing '" + clazz.getSimpleName() + "' to '" + router.getUrl(routeTarget) + "'");
			}
		}
	}

	private Set<Class<?>> getClasses(String applicationBasePath) throws IOException {
		ClassLoader classLoader = CottonServlet.class.getClassLoader();

		Set<Class<?>> classes = ConcurrentHashMap.newKeySet();
		Collections.list(classLoader.getResources(applicationBasePath))
				.parallelStream()
				.map(this::toFile)
				.forEach(dir -> classes.addAll(findClasses(dir, this.applicationBasePackage)));

		return classes;
	}

	private File toFile(URL url) {
		try {
			return new File(url.toURI().getPath());
		} catch (URISyntaxException e) {
			throw new Http500InternalServerErrorException("Unable to create file to URL", e);
		}
	}

	private Set<Class<?>> findClasses(File directory, String packageName) {
		Set<Class<?>> classes = ConcurrentHashMap.newKeySet();
		if (!directory.exists()) {
			return classes;
		}
		Arrays.asList(directory.listFiles()).parallelStream().forEach(file -> {
			if (file.isDirectory()) {
				if (!file.getName().contains(".")) {
					classes.addAll(findClasses(file, packageName + "." + file.getName()));
				}
			} else if (file.getName().endsWith(".class")) {
				classes.add(load(packageName + '.' + file.getName().replace(".class", "")));
			}
		});
		return classes;
	}

	private Class<?> load(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new Http500InternalServerErrorException("Unable to load class for automatic @" + Route.class.getSimpleName() + " detection", e);
		}
	}

	// #################################################################################################################
	// ############################################## SESSION INJECTION ################################################
	// #################################################################################################################

	@Override
	protected VaadinSession createVaadinSession(VaadinRequest request) {
		return this.serviceInjector.instantiate(CottonSession.class,
				Blueprint.SingletonAllocation.allocateToInstance(CottonServletService.SID_SERVLETSERVICE, this),
				Blueprint.PropertyAllocation.of(Bus.PROPERTY_BUS_ISOLATION, Boolean.TRUE.toString()));
	}

	@Override
	public void fireSessionDestroy(VaadinSession session) {
		if (this.serviceInjector.isActive()) {
			session.access(() -> this.serviceInjector.destroy(session));
		}
		MetricsTrailSupport.end(session.getAttribute(MetricsTrail.class));
		super.fireSessionDestroy(session);
	}

	// #################################################################################################################
	// ############################################### TRAIL THREADING #################################################
	// #################################################################################################################

	@Override
	protected List<RequestHandler> createRequestHandlers() throws ServiceException {
		List<RequestHandler> handlers = super.createRequestHandlers();

		// ADDING THE HANDLER LAST WILL MAKE IT BECOME FIRST AFTER ORDER IS INVERTED
		handlers.add((session, request, response) -> {
			session.lock();
			if (session.getAttribute(MetricsTrail.class) == null) {
				MetricsTrailSupport.begin();
				session.setAttribute(MetricsTrail.class, MetricsTrailSupport.get());

				MetricsTrailSupport.commit(CottonMetrics.SESSION_BEGIN.build(
						new MetricAttribute("sessionId", session.getSession().getId()),
						new MetricAttribute("pushSessionId", session.getPushId())));

				WebBrowser browser = session.getBrowser();
				MetricsTrailSupport.commit(CottonMetrics.SESSION_BROWSER_INFO.build(
						new MetricAttribute("application", browser.getBrowserApplication()),
						new MetricAttribute("browserType", BrowserType.of(browser.isChrome(), browser.isEdge(),
								browser.isFirefox(), browser.isIE(), browser.isOpera(), browser.isSafari()).name()),
						new MetricAttribute("browserVersion", browser.getBrowserMajorVersion() + "." + browser.getBrowserMinorVersion()),
						new MetricAttribute("systemEnvironment", SystemEnvironmentType.of(
								Null.get(browser::isAndroid, false), Null.get(browser::isIPad, false),
								Null.get(browser::isIPhone, false), Null.get(browser::isLinux, false),
								Null.get(browser::isMacOSX, false), Null.get(browser::isWindows, false),
								Null.get(browser::isWindowsPhone, false)).name())));
			} else {
				MetricsTrailSupport.bind(session.getAttribute(MetricsTrail.class));
			}
			session.unlock();

			// THE GOAL IS NOT TO HANDLE THE REQUEST, BUT TO START THE TRAIL
			return false;
		});

		return handlers;
	}

	@Override
	public void requestEnd(VaadinRequest request, VaadinResponse response, VaadinSession session) {
		if (MetricsTrailSupport.has()) {
			MetricsTrailSupport.release();
		}
		super.requestEnd(request, response, session);
	}
}