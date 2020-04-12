package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.HasAutocorrect;

/**
 * {@link EntityBuilder} for {@link HasAutocorrect} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasAutocorrect}.
 * @param <B>
 *            The final implementation type of {@link HasAutocorrectBuilder}.
 */
public interface HasAutocorrectBuilder<C extends HasAutocorrect, B extends HasAutocorrectBuilder<C, B>>
		extends EntityBuilder<C, B> {

	/**
	 * Builder method, configures whether to use autocorrection.
	 * 
	 * @see HasAutocorrect#setAutocorrect(boolean)
	 * @param autocorrect
	 *            True to use autocorrection, false otherwise.
	 * @return this
	 */
	default B setAutocorrect(boolean autocorrect) {
		return configure(hasAutorrect -> hasAutorrect.setAutocorrect(autocorrect));
	}
}
