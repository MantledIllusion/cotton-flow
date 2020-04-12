package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.HasAutocomplete;

/**
 * {@link EntityBuilder} for {@link HasAutocomplete} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasAutocomplete}.
 * @param <B>
 *            The final implementation type of {@link HasAutocompleteBuilder}.
 */
public interface HasAutocompleteBuilder<C extends HasAutocomplete, B extends HasAutocompleteBuilder<C, B>>
		extends EntityBuilder<C, B> {

	/**
	 * Builder method, configures the {@link Autocomplete} to be set.
	 * 
	 * @see HasAutocomplete#setAutocomplete(Autocomplete)
	 * @param autocomplete
	 *            The {@link Autocomplete} to use; might be null for no
	 *            autocompletion.
	 * @return this
	 */
	default B setAutocomplete(Autocomplete autocomplete) {
		return configure(hasAutocomplete -> hasAutocomplete.setAutocomplete(autocomplete));
	}
}
