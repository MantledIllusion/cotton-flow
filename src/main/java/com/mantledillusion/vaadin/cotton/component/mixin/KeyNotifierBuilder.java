package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.*;

/**
 * {@link EntityBuilder} for {@link KeyNotifier} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link KeyNotifier}.
 * @param <B>
 *            The final implementation type of {@link KeyNotifierBuilder}.
 */
public interface KeyNotifierBuilder<C extends Component & KeyNotifier, B extends KeyNotifierBuilder<C, B>>
		extends EntityBuilder<C, B> {

	/**
     * Builder method, configures a listener for {@link KeyDownEvent}s.
	 *
	 * @see KeyNotifier#addKeyDownListener(ComponentEventListener)
	 * @param listener
	 * 				The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	default B addKeyDownListener(ComponentEventListener<KeyDownEvent> listener) {
		return configure(keyNotifier -> keyNotifier.addKeyDownListener(listener));
	}

	/**
	 * Builder method, configures a listener for {@link KeyDownEvent}s.
	 *
	 * @see KeyNotifier#addKeyDownListener(Key, ComponentEventListener, KeyModifier...)
	 * @param listener
	 * 			The listener to add; might <b>not</b> be null.
	 * @param key
	 * 			The key to press; might <b>not</b> be null.
	 * @param modifiers
	 * 			The modifiers to press along with the key; might <b>not</b> be null, might be empty.
	 * @return this
	 */
	default B addKeyDownListener(ComponentEventListener<KeyDownEvent> listener, Key key, KeyModifier... modifiers) {
		return configure(keyNotifier -> keyNotifier.addKeyDownListener(key, listener, modifiers));
	}

	/**
	 * Builder method, configures a listener for {@link KeyPressEvent}s.
	 *
	 * @see KeyNotifier#addKeyPressListener(ComponentEventListener)
	 * @param listener
	 * 				The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	default B addKeyPressListener(ComponentEventListener<KeyPressEvent> listener) {
		return configure(keyNotifier -> keyNotifier.addKeyPressListener(listener));
	}

	/**
	 * Builder method, configures a listener for {@link KeyPressEvent}s.
	 *
	 * @see KeyNotifier#addKeyPressListener(Key, ComponentEventListener, KeyModifier...)
	 * @param listener
	 * 			The listener to add; might <b>not</b> be null.
	 * @param key
	 * 			The key to press; might <b>not</b> be null.
	 * @param modifiers
	 * 			The modifiers to press along with the key; might <b>not</b> be null, might be empty.
	 * @return this
	 */
	default B addKeyPressListener(ComponentEventListener<KeyPressEvent> listener, Key key, KeyModifier... modifiers) {
		return configure(keyNotifier -> keyNotifier.addKeyPressListener(key, listener, modifiers));
	}

	/**
	 * Builder method, configures a listener for {@link KeyUpEvent}s.
	 *
	 * @see KeyNotifier#addKeyUpListener(ComponentEventListener)
	 * @param listener
	 * 			The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	default B addKeyUpListener(ComponentEventListener<KeyUpEvent> listener) {
		return configure(keyNotifier -> keyNotifier.addKeyUpListener(listener));
	}

	/**
	 * Builder method, configures a listener for {@link KeyUpEvent}s.
	 *
	 * @see KeyNotifier#addKeyUpListener(Key, ComponentEventListener, KeyModifier...)
	 * @param listener
	 * 			The listener to add; might <b>not</b> be null.
	 * @param key
	 * 			The key to press; might <b>not</b> be null.
	 * @param modifiers
	 * 			The modifiers to press along with the key; might <b>not</b> be null, might be empty.
	 * @return this
	 */
	default B addKeyUpListener(ComponentEventListener<KeyUpEvent> listener, Key key, KeyModifier... modifiers) {
		return configure(keyNotifier -> keyNotifier.addKeyUpListener(key, listener, modifiers));
	}
}
