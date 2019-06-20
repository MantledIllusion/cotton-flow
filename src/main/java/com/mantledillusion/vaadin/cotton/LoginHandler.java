package com.mantledillusion.vaadin.cotton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mantledillusion.essentials.reflection.TypeEssentials;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.metrics.trail.VaadinMetricsTrailSupport;
import com.mantledillusion.metrics.trail.api.MetricAttribute;
import com.mantledillusion.vaadin.cotton.CottonUI.AfterLoginListener;
import com.mantledillusion.vaadin.cotton.CottonUI.BeforeLogoutListener;
import com.mantledillusion.vaadin.cotton.event.EventBusSubscriber;
import com.mantledillusion.vaadin.cotton.event.user.AfterLoginEvent;
import com.mantledillusion.vaadin.cotton.event.user.BeforeLogoutEvent;
import com.mantledillusion.vaadin.cotton.exception.http400.Http403UnauthorizedException;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;
import com.mantledillusion.vaadin.cotton.viewpresenter.Restricted;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import org.apache.commons.lang3.ArrayUtils;

class LoginHandler extends EventBusSubscriber implements CottonServletService.SessionBean, BeforeEnterListener {
	
	private static final long serialVersionUID = 1L;

	@Inject
	@Qualifier(CottonEnvironment.SID_LOGIN_PROVIDER)
	@Optional
	private LoginProvider provider;
	
	private User user;
	
	@Construct
	private LoginHandler() {
	}
	
	User getUser() {
		return this.user;
	}
	
	void login(User user) {
		this.user = user;
		VaadinMetricsTrailSupport.getCurrent().commit(CottonMetrics.USER_STATE.build(
				MetricAttribute.operatorOf("LOGGED_IN"),
				new MetricAttribute("user", user.toString())));
		AfterLoginEvent event = new AfterLoginEvent(CottonUI.current());
		for (AfterLoginListener listener: CottonUI.getCurrent().getNavigationListeners(AfterLoginListener.class)) {
			listener.afterLogin(event);
		}
		CottonUI.current().getPage().reload();
	}
	
	boolean logout() {
		BeforeLogoutEvent event = new BeforeLogoutEvent(CottonUI.current());
		for (BeforeLogoutListener listener: CottonUI.getCurrent().getNavigationListeners(BeforeLogoutListener.class)) {
			listener.beforeLogout(event);
			if (!event.isAccepted()) {
				return false;
			}
		}
		VaadinMetricsTrailSupport.getCurrent().commit(CottonMetrics.USER_STATE.build(
				MetricAttribute.operatorOf("LOGGED_OUT"),
				new MetricAttribute("user", this.user.toString())));
		this.user = null;
		CottonUI.current().getPage().reload();
		return true;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		List<Class<?>> restrictions = TypeEssentials.getSuperClassesAnnotatedWith(event.getNavigationTarget(),
				Restricted.class);
		
		if (!restrictions.isEmpty()) {
			if (this.user == null && this.provider != null) {
				if (this.provider.loginView != null) {
					event.rerouteTo(this.provider.loginView);
					return;
				} else if (this.provider.userProvider != null) {
					this.user = this.provider.userProvider.provide();
					VaadinMetricsTrailSupport.getCurrent().commit(CottonMetrics.USER_STATE.build(
							MetricAttribute.operatorOf("LOGGED_IN"),
							new MetricAttribute("user", user.toString())));
				}
			}
			
			if (this.user != null) {
				Set<String> requiredUserRights = new HashSet<>();
				for (Class<?> type : restrictions) {
					Restricted restricted = type.getAnnotation(Restricted.class);
					if (ArrayUtils.isNotEmpty(restricted.value())) {
						for (String requiredUserRight : restricted.value()) {
							if (requiredUserRight != null) {
								requiredUserRights.add(requiredUserRight);
							}
						}
					}
				}
				
				if (this.user.hasRights(requiredUserRights)) {
					VaadinMetricsTrailSupport.getCurrent().commit(CottonMetrics.SECURITY_ACCESS_PERMITTED.build(
							new MetricAttribute("target", event.getNavigationTarget().getName()),
							new MetricAttribute("user", this.user.toString())));
					return;
				}
			}

			VaadinMetricsTrailSupport.getCurrent().commit(CottonMetrics.SECURITY_ACCESS_DENIED.build(
					new MetricAttribute("target", event.getNavigationTarget().getName()),
					new MetricAttribute("user", this.user != null ? this.user.toString() : null)));

			event.rerouteToError(new Http403UnauthorizedException("Access to the view '"
					+ event.getNavigationTarget().getSimpleName() + "' is restricted"), null);
		}
	}
}
