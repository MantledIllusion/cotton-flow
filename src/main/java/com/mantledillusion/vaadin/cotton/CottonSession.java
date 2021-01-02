package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.vaadin.flow.server.VaadinSession;

class CottonSession extends VaadinSession {

	@Inject @Qualifier(Localizer.SID_LOCALIZER)
	private Localizer localizer;
	@Inject @Qualifier(AccessHandler.SID_NAVIGATION_HANDLER)
	private AccessHandler accessHandler;
	@Inject
	private AuthenticationHandler authenticationHandler;
	@Inject
	private Injector sessionInjector;

	@Construct
	private CottonSession(@Inject @Qualifier(CottonServletService.SID_SERVLETSERVICE) CottonServletService servletService,
						  @Inject @Qualifier(CottonErrorHandler.SID_ERROR_HANDLER) CottonErrorHandler errorHandler) {
		super(servletService);
		setErrorHandler(errorHandler);
	}

	Localizer getLocalizer() {
		return this.localizer;
	}

	AccessHandler getAccessHandler() {
		return this.accessHandler;
	}

	AuthenticationHandler getAuthenticationHandler() {
		return this.authenticationHandler;
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
