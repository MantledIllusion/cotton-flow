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
import com.mantledillusion.metrics.trail.api.Event;
import com.mantledillusion.metrics.trail.api.Measurement;
import com.mantledillusion.metrics.trail.api.MeasurementType;
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
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;
import com.vaadin.flow.shared.Registration;
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
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.jar.JarFile;

class CottonServletService extends VaadinServletService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CottonServletService.class);
	private static final String JAR = "jar";
	private static final String FILE = "file";
	private static final String FOREIGN_TRAIL = "_foreignMetricsTrail";

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
				CottonResponsiveDecider decider = new CottonResponsiveDecider((Class<? extends Component>) routeTargetType);

				if (decider.isScreenDependent()) {
					CottonResponsiveWrapper wrapper = CottonUI.current().exchangeInjectedView(CottonResponsiveWrapper.class);
					CottonUI.current().getPage().retrieveExtendedClientDetails(details ->
							wrapper.initialize(decider, details));
					target = (T) wrapper;
				} else {
					Class<? extends Component> targetViewType = decider.determineViewType().orElse(decider.routeTargetType);

					long ms = System.currentTimeMillis();
					target = (T) CottonUI.current().exchangeInjectedView(targetViewType);
					ms = System.currentTimeMillis() - ms;

					Event metric = CottonMetrics.SYSTEM_INJECTION.build(
							new Measurement("injectionDuration", String.valueOf(ms), MeasurementType.LONG),
							new Measurement("simpleName", targetViewType.getSimpleName(), MeasurementType.STRING),
							new Measurement("name", targetViewType.getName(), MeasurementType.STRING));
					if (targetViewType != decider.routeTargetType) {
						metric.getMeasurements().add(new Measurement("redirectedFromSimpleName", decider.routeTargetType.getSimpleName(), MeasurementType.STRING));
						metric.getMeasurements().add(new Measurement("redirectedFromName", decider.routeTargetType.getName(), MeasurementType.STRING));
					}
					MetricsTrailSupport.commit(metric);
				}
			} else {
				long ms = System.currentTimeMillis();
				target = CottonUI.current().exchangeInjectedView(routeTargetType);
				ms = System.currentTimeMillis() - ms;

				MetricsTrailSupport.commit(CottonMetrics.SYSTEM_INJECTION.build(
						new Measurement("injectionDuration", String.valueOf(ms), MeasurementType.LONG),
						new Measurement("simpleName", routeTargetType.getSimpleName(), MeasurementType.STRING),
						new Measurement("name", routeTargetType.getName(), MeasurementType.STRING)));
			}
			return target;
		}

		@Override
		public <T> T getOrCreate(Class<T> type) {
			return CottonServletService.this.serviceInjector.instantiate(type);
		}
	}

	private static class CottonResponsiveDecider {

		private static final List<BiPredicate<CottonResponsiveDecider, Responsive.DeviceClass>> DEVICE_MATCHERS = Arrays.asList(
				(decider, deviceClass) -> deviceClass.isAndroid() != Responsive.MatchType.UNDETERMINED
						&& decider.isDeviceAndroid == (deviceClass.isAndroid() == Responsive.MatchType.TRUE),
				(decider, deviceClass) -> deviceClass.isChromeOS() != Responsive.MatchType.UNDETERMINED
						&& decider.isDeviceChromeOS == (deviceClass.isChromeOS() == Responsive.MatchType.TRUE),
				(decider, deviceClass) -> deviceClass.isIOS() != Responsive.MatchType.UNDETERMINED
						&& decider.isDeviceIOS == (deviceClass.isIOS() == Responsive.MatchType.TRUE),
				(decider, deviceClass) -> deviceClass.isIPhone() != Responsive.MatchType.UNDETERMINED
						&& decider.isDeviceIPhone == (deviceClass.isIPhone() == Responsive.MatchType.TRUE),
				(decider, deviceClass) -> deviceClass.isIPad() != Responsive.MatchType.UNDETERMINED
						&& decider.isDeviceIPad == (deviceClass.isIPad() == Responsive.MatchType.TRUE),
				(decider, deviceClass) -> deviceClass.isLinux() != Responsive.MatchType.UNDETERMINED
						&& decider.isDeviceLinux == (deviceClass.isLinux() == Responsive.MatchType.TRUE),
				(decider, deviceClass) -> deviceClass.isMacOSX() != Responsive.MatchType.UNDETERMINED
						&& decider.isDeviceMacOSX == (deviceClass.isMacOSX() == Responsive.MatchType.TRUE),
				(decider, deviceClass) -> deviceClass.isWindows() != Responsive.MatchType.UNDETERMINED
						&& decider.isDeviceWindows == (deviceClass.isWindows() == Responsive.MatchType.TRUE),
				(decider, deviceClass) -> deviceClass.isWindowsPhone() != Responsive.MatchType.UNDETERMINED
						&& decider.isDeviceWindowsPhone == (deviceClass.isWindowsPhone() == Responsive.MatchType.TRUE)
		);

		private final Class<? extends Component> routeTargetType;
		private final Class<? extends Component>[] alternativeTargetTypes;

		private final boolean isDeviceAndroid;
		private final boolean isDeviceChromeOS;
		private final boolean isDeviceIOS;
		private final boolean isDeviceIPhone;
		private final boolean isDeviceIPad;
		private final boolean isDeviceLinux;
		private final boolean isDeviceMacOSX;
		private final boolean isDeviceWindows;
		private final boolean isDeviceWindowsPhone;

		private boolean isScreenAsyncAdaptive;
		private boolean isScreenTouchReactive;

		private CottonResponsiveDecider(Class<? extends Component> routeTargetType) {
			this.routeTargetType = routeTargetType;
			this.alternativeTargetTypes = routeTargetType.getAnnotation(Responsive.class).value();

			WebBrowser browser = CottonSession.current().getBrowser();
			this.isDeviceAndroid = browser.isAndroid();
			this.isDeviceChromeOS = browser.isChromeOS();
			this.isDeviceIOS = browser.isIOS();
			this.isDeviceIPhone = browser.isIPhone();
			this.isDeviceIPad = browser.isIPad();
			this.isDeviceLinux = browser.isLinux();
			this.isDeviceMacOSX = browser.isMacOSX();
			this.isDeviceWindows = browser.isWindows();
			this.isDeviceWindowsPhone = browser.isWindowsPhone();
		}

		private boolean isScreenDependent() {
			return Arrays.stream(this.alternativeTargetTypes)
					.anyMatch(alternativeTargetType -> alternativeTargetType.isAnnotationPresent(Responsive.ScreenClass.class));
		}

		private void initializeForScreen(ExtendedClientDetails clientDetails) {
			this.isScreenAsyncAdaptive = CottonServletService.getCurrent().ensurePushAvailable() &&
					this.routeTargetType.isAnnotationPresent(Push.class);
			this.isScreenTouchReactive = clientDetails.isTouchDevice();
		}

		private Optional<Class<? extends Component>> determineViewType() {
			return Arrays.stream(this.alternativeTargetTypes)
					.filter(alternativeTargetType -> matchesDevice(alternativeTargetType))
					.findFirst();
		}

		private Optional<Class<? extends Component>> determineViewType(int width, int height) {
			return Arrays.stream(this.alternativeTargetTypes)
					.filter(alternativeTargetType -> matchesDevice(alternativeTargetType))
					.filter(alternativeTargetType -> matchesScreen(alternativeTargetType, width, height))
					.findFirst();
		}

		private boolean matchesDevice(Class<? extends Component> alternativeTargetType) {
			if (alternativeTargetType.isAnnotationPresent(Responsive.DeviceClass.class)) {
				Responsive.DeviceClass deviceClass = alternativeTargetType.getAnnotation(Responsive.DeviceClass.class);
				if (deviceClass.andConjoined()) {
					return DEVICE_MATCHERS.stream().allMatch(predicate -> predicate.test(this, deviceClass));
				} else {
					return DEVICE_MATCHERS.stream().anyMatch(predicate -> predicate.test(this, deviceClass));
				}
			} else {
				return true;
			}
		}

		private boolean matchesScreen(Class<? extends Component> alternativeTargetType, int width, int height) {
			if (alternativeTargetType.isAnnotationPresent(Responsive.ScreenClass.class)) {
				Responsive.ScreenClass screenClass = alternativeTargetType.getAnnotation(Responsive.ScreenClass.class);

				if (this.isScreenTouchReactive && screenClass.isTouchDevice() == Responsive.MatchType.FALSE ||
						!this.isScreenTouchReactive && screenClass.isTouchDevice() == Responsive.MatchType.TRUE) {
					return false;
				}

				switch (screenClass.mode()) {
					case ABSOLUTE:
						return width >= screenClass.fromX() && width <= screenClass.toX() &&
								height >= screenClass.fromY() && height <= screenClass.toY();
					case RATIO:
						double clientRatio = width / (double) height;
						double alternativeFromRatio = Math.min(screenClass.fromX() / (double) screenClass.fromY(),
								screenClass.toX() / (double) screenClass.toY());
						double alternativeToRatio = Math.max(screenClass.fromX() / (double) screenClass.fromY(),
								screenClass.toX() / (double) screenClass.toY());
						return clientRatio >= alternativeFromRatio && clientRatio <= alternativeToRatio;
					default:
						throw new Http500InternalServerErrorException("Handling of alternative mode " +
								screenClass.mode().name() + " not implemented");
				}
			} else {
				return true;
			}
		}
	}

	static class CottonResponsiveWrapper extends Div implements BrowserWindowResizeListener {

		private final Injector injector;
		private final Registration registration;
		private final ExecutorService executorService;
		private final int responsiveAdaptionWaitMs;
		private CottonResponsiveDecider decider;
		private Pair<Integer, Integer> resizeDimension;
		private long resizeWaitMs;


		@Construct
		private CottonResponsiveWrapper(@Inject Injector injector,
										@Resolve("${" + PKEY_RESPONSIVE_ADAPTION_WAIT_MS + ":" + DEFAULT_RESPONSIVE_ADAPTION_WAIT_MS + "}") String responsiveAdaptionWaitMs) {
			this.injector = injector;
			this.registration = CottonUI.current().getPage().addBrowserWindowResizeListener(this);
			this.executorService = Executors.newSingleThreadExecutor();
			this.responsiveAdaptionWaitMs = Math.max(0, Integer.parseInt(responsiveAdaptionWaitMs));

			getElement().getStyle().set("width", "100%");
			getElement().getStyle().set("height", "100%");
		}

		private <T extends Component> void initialize(CottonResponsiveDecider decider, ExtendedClientDetails clientDetails) {
			this.decider = decider;
			this.decider.initializeForScreen(clientDetails);
			Optional<Class<? extends Component>> alternativeTargetType = this.decider.determineViewType(
					clientDetails.getWindowInnerWidth(), clientDetails.getWindowInnerHeight());
			injectAlternative(alternativeTargetType.orElse(this.decider.routeTargetType));
		}

		@Override
		public void browserWindowResized(BrowserWindowResizeEvent resizeEvent) {
			if (this.decider.isScreenAsyncAdaptive) {
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
									resizeDimension.getRight(), Responsive.ScreenClass.AdaptionMode.PERFORM));
						}
					});
				}
			} else {
				adaptIfRequired(resizeEvent.getWidth(), resizeEvent.getHeight(), Responsive.ScreenClass.AdaptionMode.PERFORM);
			}
		}

		void adaptIfRequired(int width, int height, Responsive.ScreenClass.AdaptionMode sourceAdaptionMode) {
			Class<? extends Component> targetViewType = this.decider.determineViewType(width, height)
					.orElse(this.decider.routeTargetType);
			Responsive.ScreenClass.AdaptionMode adaptionMode = Optional
					.ofNullable(targetViewType.getAnnotation(Responsive.ScreenClass.class))
					.map(Responsive.ScreenClass::automaticAdaptionMode)
					.map(targetAdaptionMode -> Responsive.ScreenClass.AdaptionMode.combine(sourceAdaptionMode, targetAdaptionMode))
					.orElse(sourceAdaptionMode);

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

		private void injectAlternative(Class<? extends Component> targetViewType) {
			removeAll();

			long ms = System.currentTimeMillis();
			add(this.injector.instantiate(targetViewType));
			ms = System.currentTimeMillis() - ms;

			Event metric = CottonMetrics.SYSTEM_INJECTION.build(
					new Measurement("injectionDuration", String.valueOf(ms), MeasurementType.LONG),
					new Measurement("simpleName", targetViewType.getSimpleName(), MeasurementType.STRING),
					new Measurement("name", targetViewType.getName(), MeasurementType.STRING));
			if (targetViewType != this.decider.routeTargetType) {
				metric.getMeasurements().add(new Measurement("redirectedFromSimpleName", this.decider.routeTargetType.getSimpleName(), MeasurementType.STRING));
				metric.getMeasurements().add(new Measurement("redirectedFromName", this.decider.routeTargetType.getName(), MeasurementType.STRING));
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

		// REGISTER NAVIGATION ERROR VIEW
		((ApplicationRouteRegistry) getRouteRegistry()).setErrorNavigationTargets(
				Collections.singleton(CottonErrorHandler.CottonErrorView.class));

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
			router.getAvailableRoutes().stream()
					.filter(route -> route.getNavigationTarget().equals(clazz))
					.findFirst()
					.ifPresent(route -> LOGGER.debug("Routing '" + clazz.getSimpleName() + "' to '" + route.getTemplate() + "'"));
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
		session.lock();
		MetricsTrailSupport.bind(session.getAttribute(MetricsTrail.class));
		MetricsTrailSupport.commit(CottonMetrics.SESSION_END.build(
				new Measurement("sessionId", session.getSession().getId(), MeasurementType.STRING),
				new Measurement("pushSessionId", session.getPushId(), MeasurementType.STRING)));
		MetricsTrailSupport.release();
		session.unlock();

		if (this.serviceInjector.isActive()) {
			session.access(() -> this.serviceInjector.destroy(session));
		}

		session.lock();
		MetricsTrailSupport.end(session.getAttribute(MetricsTrail.class));
		session.unlock();

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
				if (!MetricsTrailSupport.has()) {
					MetricsTrailSupport.begin();
					session.setAttribute(FOREIGN_TRAIL, Boolean.FALSE);
				} else {
					session.setAttribute(FOREIGN_TRAIL, Boolean.TRUE);
				}
				session.setAttribute(MetricsTrail.class, MetricsTrailSupport.get());

				MetricsTrailSupport.commit(CottonMetrics.SESSION_BEGIN.build(
						new Measurement("sessionId", session.getSession().getId(), MeasurementType.STRING),
						new Measurement("pushSessionId", session.getPushId(), MeasurementType.STRING)));

				WebBrowser browser = session.getBrowser();
				MetricsTrailSupport.commit(CottonMetrics.SESSION_BROWSER_INFO.build(
						new Measurement("application", browser.getBrowserApplication(), MeasurementType.STRING),
						new Measurement("browserType", BrowserType.of(browser.isChrome(), browser.isEdge(), browser.isFirefox(),
								browser.isIE(), browser.isOpera(), browser.isSafari()).name(), MeasurementType.STRING),
						new Measurement("browserVersion", browser.getBrowserMajorVersion() + "." + browser.getBrowserMinorVersion(), MeasurementType.STRING),
						new Measurement("systemEnvironment", SystemEnvironmentType.of(
								Null.get(browser::isAndroid, false), Null.get(browser::isIPad, false),
								Null.get(browser::isIPhone, false), Null.get(browser::isLinux, false),
								Null.get(browser::isMacOSX, false), Null.get(browser::isWindows, false),
								Null.get(browser::isWindowsPhone, false)).name(), MeasurementType.STRING)));
			} else if (!MetricsTrailSupport.has()) {
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
		if (MetricsTrailSupport.has() && session != null) {
			session.lock();
			if (!((Boolean) session.getAttribute(FOREIGN_TRAIL))) {
				MetricsTrailSupport.release();
			}
			session.unlock();
		}

		super.requestEnd(request, response, session);
	}
}