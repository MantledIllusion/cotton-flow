package com.mantledillusion.vaadin.cotton;

import java.util.*;

import com.mantledillusion.essentials.expression.Expression;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.metrics.trail.VaadinMetricsTrailSupport;
import com.mantledillusion.metrics.trail.api.MetricAttribute;
import com.mantledillusion.vaadin.cotton.CottonUI.AfterLoginListener;
import com.mantledillusion.vaadin.cotton.CottonUI.BeforeLogoutListener;
import com.mantledillusion.vaadin.cotton.event.user.AfterLoginEvent;
import com.mantledillusion.vaadin.cotton.event.user.BeforeLogoutEvent;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;

class AuthenticationHandler {

	private User user;
	
	@Construct
	private AuthenticationHandler() {
	}

	boolean isLoggedIn() {
		return this.user != null;
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
}
