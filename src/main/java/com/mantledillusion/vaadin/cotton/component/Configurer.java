package com.mantledillusion.vaadin.cotton.component;

import com.vaadin.flow.component.Component;

/**
 * {@link Configurer} for components.
 *
 * @param <C>
 *            The {@link Component} type to configure.
 */
@FunctionalInterface
public interface Configurer<C> {

	/**
	 * Configures the given {@link Component}.
	 * 
	 * @param component
	 *            The {@link Component} to configure; might <b>not</b> be null.
	 */
	void configure(C component);
}
