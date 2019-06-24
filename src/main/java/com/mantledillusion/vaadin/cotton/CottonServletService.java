package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Bus;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;
import com.mantledillusion.metrics.trail.MetricsTrailConsumer;
import com.mantledillusion.metrics.trail.VaadinMetricsTrailSupport;
import com.mantledillusion.metrics.trail.api.MetricAttribute;
import com.mantledillusion.vaadin.cotton.exception.http500.Http500InternalServerErrorException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http900NoSessionContextException;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.NavigationEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

class CottonServletService extends VaadinServletService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CottonServletService.class);
	private static final String JAR = "jar";
	private static final String FILE = "file";

	static final String SID_SERVLETSERVICE = "_servletService";

	private final class CottonInstantiator extends DefaultInstantiator {

		private static final long serialVersionUID = 1L;

		public CottonInstantiator() {
			super(CottonServletService.this);
		}

		@Override
		public I18NProvider getI18NProvider() {
			return CottonServletService.this.localizer;
		}

		@Override
		public <T extends HasElement> T createRouteTarget(Class<T> routeTargetType, NavigationEvent event) {
			long ms = System.currentTimeMillis();
			T target = super.createRouteTarget(routeTargetType, event);
			ms = System.currentTimeMillis() - ms;
			VaadinMetricsTrailSupport.getCurrent().commit(CottonMetrics.SYSTEM_INJECTION.build(ms,
					new MetricAttribute("class", routeTargetType.getName())));
			return target;
		}

		@Override
		public <T> T getOrCreate(Class<T> type) {
			return CottonSession.current().create(type);
		}
	}

	interface SessionBean {

		@SuppressWarnings("unchecked")
		default <T extends SessionBean> void hook(VaadinSession session) {
			session.setAttribute((Class<T>) getClass(), (T) this);
		}

		default void unhook(VaadinSession session) {
			session.setAttribute(getClass(), null);
		}

		static <T extends SessionBean> T current(Class<T> beanType) {
			if (VaadinSession.getCurrent() == null) {
				throw new Http900NoSessionContextException();
			}
			return VaadinSession.getCurrent().getAttribute(beanType);
		}
	}

	private final Injector serviceInjector;
	private final Localizer localizer;
	private final String applicationInitializerClass;
	private final String applicationBasePackage;
	private final boolean automaticRouteDiscovery;

	CottonServletService(@Inject @Qualifier(CottonServlet.SID_SERVLET) VaadinServlet servlet,
						 @Inject @Qualifier(CottonServlet.SID_DEPLOYMENTCONFIG) DeploymentConfiguration deploymentConfiguration,
						 @Inject @Qualifier(Localizer.SID_LOCALIZER) Localizer localizer,
						 @Inject Injector serviceInjector,
						 @Resolve("${" + CottonServlet.PID_INITIALIZERCLASS + "}") String applicationInitializerClass,
						 @Resolve("${" + CottonServlet.PID_BASEPACKAGE + "}") String applicationBasePackage,
						 @Resolve("${" + CottonEnvironment.PKEY_AUTOMATIC_ROUTE_DISCOVERY + ":false}") @Matches("(true)|(false)") String automaticRouteDiscovery) {
		super(servlet, deploymentConfiguration);
		this.serviceInjector = serviceInjector;
		this.localizer = localizer;
		this.applicationInitializerClass = applicationInitializerClass;
		this.applicationBasePackage = applicationBasePackage;
		this.automaticRouteDiscovery = Boolean.parseBoolean(automaticRouteDiscovery);
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

		// OBSERVE METRICS
		VaadinMetricsTrailSupport support = VaadinMetricsTrailSupport.support(this);
		this.serviceInjector.aggregate(MetricsTrailConsumer.class).forEach(support::hook);
	}

	private void readJar(JarFile jarFile, String applicationBasePath) {
		Collections.list(jarFile.entries()).parallelStream()
				.filter(e -> e.getName().startsWith(applicationBasePath) && e.getName().endsWith(".class"))
				.forEach(e -> registerIfRoute(load(e.getName().replace('/', '.').replace(".class", ""))));
	}

	private void registerIfRoute(Class<?> clazz) {
		if (Component.class.isAssignableFrom(clazz) && (clazz.isAnnotationPresent(Route.class) || clazz.isAnnotationPresent(RouteAlias.class))) {
			Class<? extends Component> routeTarget = (Class<? extends Component>) clazz;
			RouteConfiguration router = RouteConfiguration.forRegistry(getRouter().getRegistry());
			router.setAnnotatedRoute(routeTarget);
			LOGGER.debug("Routing '" + clazz.getSimpleName() + "' to '" + router.getUrl(routeTarget) + "'");
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

	@Override
	protected VaadinSession createVaadinSession(VaadinRequest request) {
		return this.serviceInjector.instantiate(CottonSession.class,
				Blueprint.SingletonAllocation.of(CottonServletService.SID_SERVLETSERVICE, this),
				Blueprint.PropertyAllocation.of(Bus.PROPERTY_BUS_ISOLATION, Boolean.TRUE.toString()));
	}

	@Override
	public void fireSessionDestroy(VaadinSession vaadinSession) {
		super.fireSessionDestroy(vaadinSession);
		this.serviceInjector.destroy(vaadinSession);
	}

	@Override
	protected Optional<Instantiator> loadInstantiators() {
		return Optional.of(new CottonInstantiator());
	}
}