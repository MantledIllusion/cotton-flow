package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.*;

/**
 * {@link EntityBuilder} for {@link CompositionNotifier} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link CompositionNotifier}.
 * @param <B>
 *            The final implementation type of {@link CompositionNotifierBuilder}.
 */
public interface CompositionNotifierBuilder<C extends Component & CompositionNotifier, B extends CompositionNotifierBuilder<C, B>>
		extends EntityBuilder<C, B> {

	/**
	 * Builder method, configures a listener for {@link CompositionStartEvent}s.
	 *
	 * @see CompositionNotifier#addCompositionStartListener(ComponentEventListener)
	 * @param listener
	 * 				The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	default B addCompositionStartListener(ComponentEventListener<CompositionStartEvent> listener) {
		return configure(compositionNotifier -> compositionNotifier.addCompositionStartListener(listener));
	}

	/**
	 * Builder method, configures a listener for {@link CompositionUpdateEvent}s.
	 *
	 * @see CompositionNotifier#addCompositionUpdateListener(ComponentEventListener)
	 * @param listener
	 * 				The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	default B addCompositionUpdateListener(ComponentEventListener<CompositionUpdateEvent> listener) {
		return configure(compositionNotifier -> compositionNotifier.addCompositionUpdateListener(listener));
	}

	/**
	 * Builder method, configures a listener for {@link CompositionEndEvent}s.
	 *
	 * @see CompositionNotifier#addCompositionEndListener(ComponentEventListener)
	 * @param listener
	 * 				The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	default B addCompositionEndListener(ComponentEventListener<CompositionEndEvent> listener) {
		return configure(compositionNotifier -> compositionNotifier.addCompositionEndListener(listener));
	}
}
