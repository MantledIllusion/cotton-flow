package com.mantledillusion.vaadin.cotton.metrics;

import com.mantledillusion.metrics.trail.EnumeratedMetric;
import com.mantledillusion.metrics.trail.api.Metric;
import com.mantledillusion.metrics.trail.api.MetricType;

/**
 * Enum for all {@link com.mantledillusion.metrics.trail.api.Metric} types that are dispatched by Cotton.
 */
public enum CottonMetrics implements EnumeratedMetric {

    /**
     * {@link Metric} ID for the {@link MetricType#METER} of the duration it took Cotton to inject a specific component.
     * <p>
     * Metric ID: cotton.system.injection
     * <p>
     * Properties:<br>
     * - {@link Metric#OPERATOR_ATTRIBUTE_KEY}: The duration in milliseconds it took to inject the component.<br>
     * - 'class': The {@link Class} of the injected component.<br>
     */
    SYSTEM_INJECTION(MetricType.METER),

    /**
     * {@link Metric} ID for the {@link MetricType#PHASE} of a user logging in our out.
     * <p>
     * Metric ID: cotton.user.state
     * <p>
     * Properties:<br>
     * - {@link Metric#OPERATOR_ATTRIBUTE_KEY}: Either "LOGGED_IN" or "LOGGED_OUT".<br>
     * - 'user': The identifier of the user.<br>
     */
    USER_STATE(MetricType.PHASE),

    /**
     * {@link Metric} ID for the {@link MetricType#ALERT} when acces to a view is permitted.
     * <p>
     * Metric ID: cotton.security.access.permitted
     * <p>
     * Properties:<br>
     * - 'target': The {@link Class} of the view access was permitted to.<br>
     * - 'user': The identifier of the user whose access was permitted.<br>
     */
    SECURITY_ACCESS_PERMITTED(MetricType.ALERT),

    /**
     * {@link Metric} ID for the {@link MetricType#ALERT} when acces to a view is denied.
     * <p>
     * Metric ID: cotton.security.access.denied
     * <p>
     * Properties:<br>
     * - 'target': The {@link Class} of the view access was denied to.<br>
     * - 'user': The identifier of the user whose access was denied (may be null).<br>
     */
    SECURITY_ACCESS_DENIED(MetricType.ALERT);

    private static final String METRICS_DOMAIN = "cotton.";

    private final String metricId;
    private final MetricType type;

    CottonMetrics(MetricType type) {
        this.metricId = generateMetricId(METRICS_DOMAIN, this);
        this.type = type;
    }

    @Override
    public String getMetricId() {
        return this.metricId;
    }

    @Override
    public MetricType getType() {
        return type;
    }
}
