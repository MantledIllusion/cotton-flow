package com.mantledillusion.vaadin.cotton.metrics;

import com.mantledillusion.metrics.trail.EnumeratedEvent;
import com.mantledillusion.metrics.trail.api.Event;
import com.mantledillusion.metrics.trail.api.Measurement;

/**
 * Enum for all {@link Event} types that are dispatched by Cotton.
 */
public enum CottonMetrics implements EnumeratedEvent {

    /**
     * ID for the {@link Event} an observer creates when a new session is started.
     * <p>
     * Metric ID: cotton.session.begin
     * <p>
     * Contains the {@link Measurement} :<br>
     * - 'sessionId': The session's ID.<br>
     * - 'pushSessionId': The async push session's ID.<br>
     */
    SESSION_BEGIN,

    /**
     * ID for the {@link Event} an observer creates when a session ends.
     * <p>
     * Metric ID: cotton.session.end
     * <p>
     * Contains the {@link Measurement} :<br>
     * - 'sessionId': The session's ID.<br>
     * - 'pushSessionId': The async push session's ID.<br>
     */
    SESSION_END,

    /**
     * ID for the {@link Event} an observer creates about the browser beginning a session.
     * <p>
     * Metric ID: cotton.session.browser.info
     * <p>
     * Contains the {@link Measurement} :<br>
     * - 'application': The application name<br>
     * - 'browserType': The {@link BrowserType}<br>
     * - 'browserVersion': The browser's version<br>
     * - 'systemEnvironment': The {@link SystemEnvironmentType}<br>
     */
    SESSION_BROWSER_INFO,

    /**
     * ID for the {@link Event} an observer creates when the URL changes.
     * <p>
     * Metric ID: cotton.session.navigation
     * <p>
     * Contains the {@link Measurement} :<br>
     * - 'simpleName': The simple {@link Class} name of the view access was permitted to.<br>
     * - 'name': The fully qualified {@link Class} name of the view access was permitted to.<br>
     * - 'path': The path navigated to<br>
     * - '?[query parameter key]' : Query parameter values, comma separated<br>
     */
    SESSION_NAVIGATION,

    /**
     * ID for the {@link Event} an {@link com.vaadin.flow.server.ErrorHandler} creates when
     * an uncatched {@link Throwable} occurs.
     * <p>
     * Metric ID: cotton.session.error
     * <p>
     * Contains the {@link Measurement} :<br>
     * - 'simpleName': The simple name of the {@link Throwable}'s class<br>
     * - 'name': The fully qualified class name of the {@link Throwable}'s class<br>
     * - 'message': The {@link Throwable}'s message<br>
     */
    SESSION_ERROR,

    /**
     * ID for the {@link Event} of the duration it took Cotton to inject a specific component.
     * <p>
     * Metric ID: cotton.system.injection
     * <p>
     * Contains the {@link Measurement} :<br>
     * - 'injectionDuration': The duration in milliseconds it took to inject the component.<br>
     * - 'simpleName': The simple {@link Class} name of the injected component.<br>
     * - 'name': The fully qualified {@link Class} name of the injected component.<br>
     * - 'redirectedFromSimpleName': The simple {@link Class} name that was redirected from.<br>
     * - 'redirectedFromName': The fully qualified {@link Class} name that was redirected from.<br>
     */
    SYSTEM_INJECTION,

    /**
     * ID for the {@link Event} of a user logging in our out.
     * <p>
     * Metric ID: cotton.security.user.state
     * <p>
     * Contains the {@link Measurement} :<br>
     * - 'state': Either "LOGGED_IN" or "LOGGED_OUT".<br>
     * - 'user': The identifier of the user.<br>
     */
    SECURITY_USER_STATE,

    /**
     * ID for the {@link Event} when acces to a view is permitted.
     * <p>
     * Metric ID: cotton.security.access.permitted
     * <p>
     * Contains the {@link Measurement} :<br>
     * - 'simpleName': The simple {@link Class} name of the view access was permitted to.<br>
     * - 'name': The fully qualified {@link Class} name of the view access was permitted to.<br>
     * - 'user': The identifier of the user whose access was permitted.<br>
     */
    SECURITY_ACCESS_GRANTED,

    /**
     * ID for the {@link Event} when acces to a view is denied.
     * <p>
     * Metric ID: cotton.security.access.denied
     * <p>
     * Contains the {@link Measurement} :<br>
     * - 'simpleName': The simple {@link Class} name of the view access was denied to.<br>
     * - 'name': The fully qualified {@link Class} name of the view access was denied to.<br>
     * - 'user': The identifier of the user whose access was denied (may be null).<br>
     */
    SECURITY_ACCESS_DENIED;

    @Override
    public String getPrefix() {
        return "cotton";
    }
}
