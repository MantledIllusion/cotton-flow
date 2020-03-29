package com.mantledillusion.vaadin.cotton;

import java.util.Arrays;
import java.util.List;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Bus;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;
import com.mantledillusion.metrics.trail.VaadinMetricsTrailSupport;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.VaadinSession;

class CottonSession extends VaadinSession {

	private static final long serialVersionUID = 1L;

	@Inject
	private Injector injector;
	
	private final List<CottonServletService.SessionBean> sessionBeans;

	@Construct
	private CottonSession(@Inject @Qualifier(CottonServletService.SID_SERVLETSERVICE) CottonServletService servletService,
			@Inject @Qualifier(Localizer.SID_LOCALIZER) Localizer localizer,
			@Inject LoginHandler loginHandler) {
		super(servletService);
		this.sessionBeans = Arrays.asList(localizer, loginHandler);
		setErrorHandler(getErrorHandler());
	}

	@PostInject
	private void startup() {
		for (CottonServletService.SessionBean bean: this.sessionBeans) {
			bean.hook(this);
		}
	}

	@PreDestroy
	private void shutdown() {
		for (CottonServletService.SessionBean bean: this.sessionBeans) {
			bean.unhook(this);
		}
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		super.setErrorHandler(VaadinMetricsTrailSupport.support(errorHandler));
	}

	<T> T createInSessionContext(Class<T> type) {
		return this.injector.instantiate(type);
	}

	<T> void destroyInSessionContext(T bean) {
		this.injector.destroy(bean);
	}
	
	static CottonSession current() {
		return (CottonSession) getCurrent();
	}
}
