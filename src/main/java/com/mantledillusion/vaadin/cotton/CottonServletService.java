package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.vaadin.cotton.exception.http900.Http900NoSessionContextException;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;
import com.mantledillusion.vaadin.metrics.MetricsDispatcherFlow;
import com.mantledillusion.vaadin.metrics.api.MetricAttribute;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.NavigationEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.*;

import java.util.Optional;
import java.util.function.BiPredicate;

class CottonServletService extends VaadinServletService {

	private static final long serialVersionUID = 1L;
	private static final BiPredicate<String, Class> ROUTE_COMPONENT_PREDICATE = (qualifier, componentClass) ->
		Component.class.isAssignableFrom(componentClass) && componentClass.isAnnotationPresent(Route.class);

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
			MetricsDispatcherFlow.dispatch(CottonMetrics.SYSTEM_INJECTION.build(
					new MetricAttribute("viewClass", routeTargetType.getName()),
					new MetricAttribute("duration", String.valueOf(ms))));
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

	CottonServletService(@Inject @Qualifier(CottonServlet.SID_SERVLET) VaadinServlet servlet,
						 @Inject @Qualifier(CottonServlet.SID_DEPLOYMENTCONFIG) DeploymentConfiguration deploymentConfiguration,
						 @Inject @Qualifier(Localizer.SID_LOCALIZER) Localizer localizer,
						 @Inject Injector serviceInjector) {
		super(servlet, deploymentConfiguration);
		this.serviceInjector = serviceInjector;
		this.localizer = localizer;
	}

	@Override
	public void init() throws ServiceException {
		super.init();
		try {
			for (Class<?> view: this.serviceInjector.aggregate(Class.class, ROUTE_COMPONENT_PREDICATE)) {
				RouteConfiguration.forRegistry(getRouter().getRegistry()).setAnnotatedRoute((Class<? extends Component>) view);
			}
		} catch (InvalidRouteConfigurationException e) {
			throw new ServiceException("Cannot set up routing for the given views", e);
		}
	}

	@Override
	protected VaadinSession createVaadinSession(VaadinRequest request) {
		return this.serviceInjector.instantiate(CottonSession.class,
				Blueprint.SingletonAllocation.of(CottonServletService.SID_SERVLETSERVICE, this));
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