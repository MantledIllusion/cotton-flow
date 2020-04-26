package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.metrics.trail.VaadinMetricsTrailSupport;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.VaadinSession;

class CottonSession extends VaadinSession {

	private static final long serialVersionUID = 1L;

	@Inject @Qualifier(Localizer.SID_LOCALIZER)
	private Localizer localizer;
	@Inject
	private LoginHandler loginHandler;
	@Inject
	private Injector sessionInjector;

	@Construct
	private CottonSession(@Inject @Qualifier(CottonServletService.SID_SERVLETSERVICE) CottonServletService servletService) {
		super(servletService);
		setErrorHandler(getErrorHandler());
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		super.setErrorHandler(VaadinMetricsTrailSupport.support(errorHandler));
	}

	Localizer getLocalizer() {
		return this.localizer;
	}

	LoginHandler getLoginHandler() {
		return this.loginHandler;
	}

	<T> T createInSessionContext(Class<T> type) {
		return this.sessionInjector.instantiate(type);
	}

	<T> void destroyInSessionContext(T bean) {
		if (this.sessionInjector.isActive()) {
			this.sessionInjector.destroy(bean);
		}
	}
	
	static CottonSession current() {
		return (CottonSession) getCurrent();
	}
}
