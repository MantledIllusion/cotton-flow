package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.essentials.expression.Expression;
import com.mantledillusion.essentials.reflection.TypeEssentials;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.metrics.trail.MetricsTrailSupport;
import com.mantledillusion.metrics.trail.api.Event;
import com.mantledillusion.metrics.trail.api.Measurement;
import com.mantledillusion.metrics.trail.api.MeasurementType;
import com.mantledillusion.vaadin.cotton.exception.http400.Http403UnauthorizedException;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;
import com.mantledillusion.vaadin.cotton.viewpresenter.Restricted;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveListener;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

final class AccessHandler implements BeforeLeaveListener {

    private static class Destination {

        private final int priority;
        private final Class<? extends Component> navigationTarget;
        private final RestrictionType restrictionType;
        private final List<Expression<String>> restrictions;

        private Destination(Class<? extends Component> navigationTarget, int priority,
                            RestrictionType restrictionType, List<Expression<String>> restrictions) {
            this.navigationTarget = navigationTarget;
            this.priority = priority;
            this.restrictionType = restrictionType;
            this.restrictions = restrictions;
        }

        private Class<? extends Component> getNavigationTarget() {
            return this.navigationTarget;
        }

        private int getPriority() {
            return this.priority;
        }

        private RestrictionType getRestrictionType() {
            return this.restrictionType;
        }

        private List<Expression<String>> getRestrictions() {
            return this.restrictions;
        }
    }

    private enum RestrictionType {
        NONE,
        AUTHENTICATION,
        AUTHORIZATION
    }

    static final class ForwardingView extends Div {

        @Construct
        private ForwardingView() {

        }
    }

    static final String SID_NAVIGATION_HANDLER = "_navigationHandler";

    @Inject
    @Qualifier(CottonEnvironment.SID_LOGIN_PROVIDER)
    @Optional
    private LoginProvider provider;

    private final Map<String, List<Destination>> forwardingRegistry = new HashMap<>();

    @Construct
    private AccessHandler() {}

    void register(String path, Class<? extends Component> navigationTarget, int priority) {
        this.forwardingRegistry.computeIfAbsent(path, p -> new ArrayList<>()).add(toTarget(navigationTarget, priority));
        this.forwardingRegistry.get(path).sort(Comparator.comparingInt(Destination::getPriority));
    }

    private Destination toTarget(Class<? extends Component> navigationTarget, int priority) {
        RestrictionType restrictionType = RestrictionType.NONE;
        List<Expression<String>> restrictions = new ArrayList<>();
        for (Class<?> type : TypeEssentials.getSuperClassesAnnotatedWith(navigationTarget, Restricted.class)) {
            Restricted restricted = type.getAnnotation(Restricted.class);
            if (StringUtils.isNotBlank(restricted.value())) {
                restrictions.add(Expression.parse(restricted.value()));
                restrictionType = RestrictionType.AUTHORIZATION;
            } else if (restrictionType == RestrictionType.NONE) {
                restrictionType = RestrictionType.AUTHENTICATION;
            }
        }
        return new Destination(navigationTarget, priority, restrictionType, restrictions);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (this.provider != null && this.provider.loginView != null &&
                this.provider.loginView == event.getNavigationTarget()) {
            return;
        }

        List<Destination> possibleDestinations;
        if (ForwardingView.class.isAssignableFrom(event.getNavigationTarget())) {
            possibleDestinations = this.forwardingRegistry.get(event.getLocation().getPath());
        } else {
            possibleDestinations = Collections.singletonList(
                    toTarget((Class<? extends Component>) event.getNavigationTarget(), 0));
        }

        boolean isAuthenticationDesirable = possibleDestinations.stream().
                anyMatch(possibleDestination -> possibleDestination.getRestrictionType() != RestrictionType.NONE);
        boolean isAuthenticationNecessary = possibleDestinations.stream().
                allMatch(possibleDestination -> possibleDestination.getRestrictionType() != RestrictionType.NONE);
        AuthenticationHandler authenticationHandler = CottonSession.current().getAuthenticationHandler();
        if (isAuthenticationDesirable && !authenticationHandler.isLoggedIn()) {
            if (this.provider.userProvider != null && (this.provider.userProvider.isSilent() || isAuthenticationNecessary)) {
                User user = this.provider.userProvider.provide();
                if (user != null) {
                    authenticationHandler.login(user);
                }
            } else if (this.provider.loginView != null && isAuthenticationNecessary) {
                event.rerouteTo(this.provider.loginView);
                return;
            }
        }

        Destination destination = null;
        for (Destination possibleTarget: possibleDestinations) {
            if ((possibleTarget.getRestrictionType() == RestrictionType.NONE) || (authenticationHandler.isLoggedIn() && (
                    (possibleTarget.getRestrictionType() == RestrictionType.AUTHENTICATION) ||
                    (possibleTarget.getRestrictions().stream().allMatch(authenticationHandler::userHasRights))))) {
                destination = possibleTarget;
                break;
            }
        }

        if (destination == null) {
            MetricsTrailSupport.commit(CottonMetrics.SECURITY_ACCESS_DENIED.build(
                    new Measurement("simpleName", event.getNavigationTarget().getSimpleName(), MeasurementType.STRING),
                    new Measurement("name", event.getNavigationTarget().getName(), MeasurementType.STRING),
                    new Measurement("user", authenticationHandler.isLoggedIn() ?
                            authenticationHandler.getUser().toString() : null, MeasurementType.STRING)));

            event.rerouteToError(new Http403UnauthorizedException("Access to the view '"
                    + event.getNavigationTarget().getSimpleName() + "' is restricted"), null);
        } else {
            if (destination.getRestrictionType() != RestrictionType.NONE) {
                MetricsTrailSupport.commit(CottonMetrics.SECURITY_ACCESS_GRANTED.build(
                        new Measurement("simpleName", event.getNavigationTarget().getSimpleName(), MeasurementType.STRING),
                        new Measurement("name", event.getNavigationTarget().getName(), MeasurementType.STRING),
                        new Measurement("user", authenticationHandler.getUser().toString(), MeasurementType.STRING)));
            }
            if (event.getNavigationTarget() != destination.getNavigationTarget()) {
                event.forwardTo(destination.getNavigationTarget());
            } else {
                java.util.Optional<String> url = event.getSource().getRegistry().getTargetUrl(destination.getNavigationTarget());
                if (url.isPresent()) {
                    Event metric = CottonMetrics.SESSION_NAVIGATION.build(new Measurement("url", url.get(), MeasurementType.STRING));
                    String query = event.getLocation().getQueryParameters().getQueryString();
                    if (!query.isEmpty()) {
                        for (Map.Entry<String, String> param : fromParamAppender(query).entrySet()) {
                            metric.getMeasurements().add(new Measurement(param.getKey(), param.getValue(), MeasurementType.STRING));
                        }
                    }
                    MetricsTrailSupport.commit(metric);
                }
            }
        }
    }

    private static Map<String, String> fromParamAppender(String query) {
        Map<String, String> params = new HashMap<>();
        for (String param : query.split("&")) {
            String[] splitted = param.split("=");
            params.put(splitted[0], splitted[1]);
        }
        return params;
    }
}
