package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;

/**
 * {@link ComponentBuilder} for {@link HasEnabled} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasEnabled}.
 * @param <B>
 *            The final implementation type of {@link HasEnabledBuilder}.
 */
public interface HasEnabledBuilder<C extends HasEnabled, B extends HasEnabledBuilder<C, B>>
		extends ComponentBuilder<C, B> {

	/**
	 * Builder method, configures whether the {@link Component} is enabled or
	 * disabled after building.
	 * 
	 * @see HasEnabled#setEnabled(boolean)
	 * @param enabled
	 *            True if the {@link Component} has to be enabled, false otherwise.
	 * @return this
	 */
	default B setEnabled(boolean enabled) {
		return configure(hasEnabled -> hasEnabled.setEnabled(enabled));
	}
}
