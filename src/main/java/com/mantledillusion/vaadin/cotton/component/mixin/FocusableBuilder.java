package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;

/**
 * {@link EntityBuilder} for {@link Focusable} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link Focusable}.
 * @param <B>
 *            The final implementation type of {@link FocusableBuilder}.
 */
public interface FocusableBuilder<C extends Component & Focusable<C>, B extends FocusableBuilder<C, B>>
		extends EntityBuilder<C, B> {

	/**
	 * Builder method, configures the index in whose order the {@link Component}
	 * gets focused when the user is stepping through them using the TAB key.
	 * 
	 * @see Focusable#setTabIndex(int)
	 * @param tabIndex
	 *            The index of the {@link Component}.
	 * @return this
	 */
	default B setTabIndex(int tabIndex) {
		return configure(focusable -> focusable.setTabIndex(tabIndex));
	}
}
