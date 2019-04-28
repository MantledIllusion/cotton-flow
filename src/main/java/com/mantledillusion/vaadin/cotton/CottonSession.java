package com.mantledillusion.vaadin.cotton;

import java.util.Arrays;
import java.util.List;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;
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

	<T> T create(Class<T> type) {
		this.injector.destroyAll();
		return this.injector.instantiate(type);
	}
	
	static CottonSession current() {
		return (CottonSession) getCurrent();
	}
}
