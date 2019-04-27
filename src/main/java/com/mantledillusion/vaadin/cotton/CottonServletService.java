package com.mantledillusion.vaadin.cotton;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.ListUtils;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Injector.RootInjector;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.vaadin.cotton.CottonServlet.TemporalCottonServletConfiguration;
import com.mantledillusion.vaadin.cotton.exception.http900.Http900NoSessionContextException;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;
import com.mantledillusion.vaadin.metrics.MetricsDispatcherFlow;
import com.mantledillusion.vaadin.metrics.api.MetricAttribute;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.InvalidRouteConfigurationException;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;

class CottonServletService extends VaadinServletService {

	private static final long serialVersionUID = 1L;

	static final String SID_SERVLETSERVICE = "_servletService";

	private final class CottonInstantiator extends DefaultInstantiator {

		private static final long serialVersionUID = 1L;

		public CottonInstantiator(VaadinService service) {
			super(service);
		}

		@Override
		public I18NProvider getI18NProvider() {
			return CottonServletService.this.localizer;
		}

		@Override
		public <T> T getOrCreate(Class<T> type) {
			long ms = System.currentTimeMillis();
			T view = CottonSession.current().create(type);
			ms = System.currentTimeMillis() - ms;
			MetricsDispatcherFlow.dispatch(CottonMetrics.SYSTEM_INJECTION.build(
					new MetricAttribute("viewClass", type.getName()), 
					new MetricAttribute("duration", String.valueOf(ms))));
			return view;
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

	private final Set<Class<? extends Component>> views;
	private final Localizer localizer;
	private final RootInjector serviceInjector;

	CottonServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration,
			TemporalCottonServletConfiguration config) {
		super(servlet, deploymentConfiguration);
		this.views = config.getViews();
		this.localizer = config.getLocalizerBuilder().build();

		Singleton servletService = Singleton.of(SID_SERVLETSERVICE, this);
		Singleton localizer = Singleton.of(Localizer.SID_LOCALIZER, this.localizer);
		Singleton loginProvider = Singleton.of(LoginProvider.SID_LOGIN_PROVIDER, config.getLoginProvider());
		this.serviceInjector = Injector.of(
				ListUtils.union(config.getPredefinables(), Arrays.asList(servletService, localizer, loginProvider)));
	}

	@Override
	public void init() throws ServiceException {
		super.init();
		try {
			for (Class<? extends Component> view: this.views) {
				RouteConfiguration.forRegistry(getRouter().getRegistry()).setAnnotatedRoute(view);
			}
		} catch (InvalidRouteConfigurationException e) {
			throw new ServiceException("Cannot set up routing for the given views", e);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		this.serviceInjector.destroyInjector();
	}

	@Override
	protected VaadinSession createVaadinSession(VaadinRequest request) {
		return this.serviceInjector.instantiate(CottonSession.class);
	}

	@Override
	public void fireSessionDestroy(VaadinSession vaadinSession) {
		super.fireSessionDestroy(vaadinSession);
		this.serviceInjector.destroy(vaadinSession);
	}

	@Override
	protected Optional<Instantiator> loadInstantiators() throws ServiceException {
		return Optional.of(new CottonInstantiator(this));
	}
}