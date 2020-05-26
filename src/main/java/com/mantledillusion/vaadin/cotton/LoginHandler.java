package com.mantledillusion.vaadin.cotton;

import java.util.*;

import com.mantledillusion.essentials.expression.Expression;
import com.mantledillusion.essentials.reflection.TypeEssentials;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.metrics.trail.VaadinMetricsTrailSupport;
import com.mantledillusion.metrics.trail.api.MetricAttribute;
import com.mantledillusion.vaadin.cotton.CottonUI.AfterLoginListener;
import com.mantledillusion.vaadin.cotton.CottonUI.BeforeLogoutListener;
import com.mantledillusion.vaadin.cotton.event.user.AfterLoginEvent;
import com.mantledillusion.vaadin.cotton.event.user.BeforeLogoutEvent;
import com.mantledillusion.vaadin.cotton.exception.http400.Http403UnauthorizedException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;
import com.mantledillusion.vaadin.cotton.viewpresenter.Restricted;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveListener;
import org.apache.commons.lang3.StringUtils;

class LoginHandler implements BeforeLeaveListener {
	
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

	boolean userHasRights(Expression<String> rightExpression) {
		if (rightExpression == null) {
			throw new Http901IllegalArgumentException("Unable to check user rights against a null expression");
		} else if (this.user == null) {
			return false;
		}
		return rightExpression.evaluate(rightId -> this.user.hasRights(Collections.singleton(rightId)));
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		if (this.provider != null && this.provider.loginView != null &&
				this.provider.loginView == event.getNavigationTarget()) {
			return;
		}

		List<Class<?>> restrictedTypes = TypeEssentials.getSuperClassesAnnotatedWith(event.getNavigationTarget(),
				Restricted.class);
		
		if (!restrictedTypes.isEmpty()) {
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
				List<Expression<String>> restrictions = new ArrayList<>();
				for (Class<?> type : restrictedTypes) {
					Restricted restricted = type.getAnnotation(Restricted.class);
					if (StringUtils.isNotBlank(restricted.value())) {
						restrictions.add(Expression.parse(restricted.value()));
					}
				}
				
				if (restrictions.stream().allMatch(this::userHasRights)) {
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
