package com.mantledillusion.vaadin.cotton.metrics;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.vaadin.metrics.api.Metric;
import com.mantledillusion.vaadin.metrics.api.MetricAttribute;
import com.mantledillusion.vaadin.metrics.api.MetricType;

/**
 * Enum for all {@link Metric} types that are thrown by cotton.
 * <p>
 * The {@link Metric#getIdentifier()} of each entry is always the
 * {@link Enum#name()} in lower case with '_' replaced to '.' and prefixed by
 * {@value #METRICS_DOMAIN}.
 * <p>
 * For example the name of {@link #SYSTEM_INJECTION} is
 * 'cotton.system.injection'.
 */
public enum CottonMetrics {

	/**
	 * Dispatched when the injection of a view to navigate to took place.
	 * <p>
	 * Properties:<br>
	 * - 'viewClass': The {@link Class} of the injected view.<br>
	 * - 'duration': The duration in milliseconds it took to inject the view.<br>
	 */
	SYSTEM_INJECTION(MetricType.ALERT);

	private static final String METRICS_DOMAIN = "cotton.";

	private final MetricType type;
	private final String name;

	private CottonMetrics(MetricType type) {
		this.type = type;
		this.name = METRICS_DOMAIN + StringUtils.join(name().toLowerCase().split("_"), '.');
	}

	/**
	 * Returns the {@link MetricType} of the metric.
	 * 
	 * @return The type, never null
	 */
	public MetricType getType() {
		return type;
	}

	/**
	 * Returns the name of the metric.
	 * 
	 * @return The name, never null
	 */
	public String getName() {
		return name;
	}

	/**
	 * Creates a new {@link Metric} of this type.
	 * 
	 * @param attributes
	 *            The attributes of the event; might be null or contain nulls.
	 * @return A new {@link Metric} instance, never null
	 */
	public Metric build(MetricAttribute... attributes) {
		Metric event = new Metric();
		event.setIdentifier(this.name);
		event.setType(this.type);
		Arrays.stream(attributes).forEach(attribute -> {
			if (attribute != null)
				event.getAttributes().add(attribute);
		});
		return event;
	}
}
