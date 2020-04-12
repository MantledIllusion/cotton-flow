package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.HasAutocapitalize;

/**
 * {@link EntityBuilder} for {@link HasAutocapitalize} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasAutocapitalize}.
 * @param <B>
 *            The final implementation type of {@link HasAutocapitalizeBuilder}.
 */
public interface HasAutocapitalizeBuilder<C extends HasAutocapitalize, B extends HasAutocapitalizeBuilder<C, B>>
		extends EntityBuilder<C, B> {

	/**
	 * Builder method, configures the {@link Autocapitalize} to be set.
	 * 
	 * @see HasAutocapitalize#setAutocapitalize(Autocapitalize)
	 * @param autocapitalize
	 *            The {@link Autocapitalize} to use; might be null for no
	 *            autocapitalization.
	 * @return this
	 */
	default B setAutocapitalize(Autocapitalize autocapitalize) {
		return configure(hasAutocomplete -> hasAutocomplete.setAutocapitalize(autocapitalize));
	}
}
