package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * {@link EntityBuilder} for {@link HasValueChangeMode} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing
 *            {@link HasValueChangeMode}.
 * @param <B>
 *            The final implementation type of
 *            {@link HasValueChangeModeBuilder}.
 */
public interface HasValueChangeModeBuilder<C extends HasValueChangeMode, B extends HasValueChangeModeBuilder<C, B>>
		extends EntityBuilder<C, B> {

	/**
	 * Builder method, configures the {@link ValueChangeMode} that specifies at what
	 * point a changed value will be synchronized between client and server.
	 * 
	 * @see HasValueChangeMode#setValueChangeMode(ValueChangeMode)
	 * @param mode
	 *            The {@link ValueChangeMode} to set; might be null to disable value
	 *            synchronization.
	 * @return this
	 */
	default B setValueChangeMode(ValueChangeMode mode) {
		return configure(hasValueChangeMode -> hasValueChangeMode.setValueChangeMode(mode));
	}
}
