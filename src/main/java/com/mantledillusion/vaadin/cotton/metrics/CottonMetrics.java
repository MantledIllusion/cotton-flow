package com.mantledillusion.vaadin.cotton.metrics;

import com.mantledillusion.vaadin.metrics.EnumeratedMetric;

import com.mantledillusion.vaadin.metrics.api.Metric;
import com.mantledillusion.vaadin.metrics.api.MetricType;

/**
 * Enum for all {@link Metric} types that are thrown by Cotton.
 */
public enum CottonMetrics implements EnumeratedMetric {

    /**
     * Dispatched when the injection of a view to navigate to took place.
     * <p>
     * Metric ID: cotton.system.injection
     * <p>
     * Properties:<br>
     * - 'viewClass': The {@link Class} of the injected {@link com.vaadin.flow.component.Component}.<br>
     * - 'duration': The duration in milliseconds it took to inject the view.<br>
     */
    SYSTEM_INJECTION(MetricType.ALERT);

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
