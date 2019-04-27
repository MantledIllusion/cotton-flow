package com.mantledillusion.vaadin.cotton;

import java.util.Arrays;
import java.util.List;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Construct;
import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Process;
import com.mantledillusion.vaadin.cotton.CottonServletService.SessionBean;
import com.vaadin.flow.server.VaadinSession;

class CottonSession extends VaadinSession {

	private static final long serialVersionUID = 1L;

	@Inject
	private Injector injector;
	
	private final List<SessionBean> sessionBeans;

	@Construct
	private CottonSession(@Inject(CottonServletService.SID_SERVLETSERVICE) @Global CottonServletService servletService,
			@Inject(Localizer.SID_LOCALIZER) @Global Localizer localizer,
			@Inject LoginHandler loginHandler) {
		super(servletService);
		this.sessionBeans = Arrays.asList(localizer, loginHandler);
	}

	@Process(Phase.INJECT)
	private void startup() {
		for (SessionBean bean: this.sessionBeans) {
			bean.hook(this);
		}
	}

	@Process(Phase.DESTROY)
	private void shutdown() {
		for (SessionBean bean: this.sessionBeans) {
			bean.unhook(this);
		}
	}

	<T> T create(Class<T> type) {
		this.injector.destroyAll();
		return this.injector.instantiate(type);
	}
	
	static CottonSession current() {
		return (CottonSession) getCurrent();
	}
}
