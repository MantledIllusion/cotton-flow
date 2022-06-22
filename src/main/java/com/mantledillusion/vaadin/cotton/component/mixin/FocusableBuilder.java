package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.*;

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

	/**
	 * Builder method, configures a listener for {@link BlurNotifier.BlurEvent}s.
	 *
	 * @param listener
	 * 			The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	default B addBlurListener(ComponentEventListener<BlurNotifier.BlurEvent<C>> listener) {
		return configure(focusable -> focusable.addBlurListener(listener));
	}

	/**
	 * Builder method, configures a listener for {@link FocusNotifier.FocusEvent}s.
	 *
	 * @param listener
	 * 			The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	default B addFocusListener(ComponentEventListener<FocusNotifier.FocusEvent<C>> listener) {
		return configure(focusable -> focusable.addFocusListener(listener));
	}
}
