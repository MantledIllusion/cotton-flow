package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.metrics.trail.api.Metric;
import com.mantledillusion.metrics.trail.api.MetricAttribute;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.VaadinSession;

import java.io.PrintWriter;
import java.io.StringWriter;

class CottonSession extends VaadinSession {

	private static final long serialVersionUID = 1L;

	@Inject @Qualifier(Localizer.SID_LOCALIZER)
	private Localizer localizer;
	@Inject @Qualifier(AccessHandler.SID_NAVIGATION_HANDLER)
	private AccessHandler accessHandler;
	@Inject
	private AuthenticationHandler authenticationHandler;
	@Inject
	private Injector sessionInjector;

	@Construct
	private CottonSession(@Inject @Qualifier(CottonServletService.SID_SERVLETSERVICE) CottonServletService servletService) {
		super(servletService);
		setErrorHandler(getErrorHandler());
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		if (errorHandler == null) {
			throw new IllegalArgumentException("Cannot set a null error handler");
		}
		super.setErrorHandler(error -> {
			Throwable t = error.getThrowable();
			StringWriter out = new StringWriter();
			PrintWriter writer = new PrintWriter(out);
			t.printStackTrace(writer);

			Metric metric = CottonMetrics.SESSION_ERROR.build(t.getClass().getSimpleName());
			metric.getAttributes().add(new MetricAttribute("type", t.getClass().getName()));
			metric.getAttributes().add(new MetricAttribute("message", t.getMessage()));
			metric.getAttributes().add(new MetricAttribute("stackTrace", out.toString()));

			errorHandler.error(error);
		});
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
