package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.*;

/**
 * {@link EntityBuilder} for {@link ClickNotifier} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link ClickNotifier}.
 * @param <B>
 *            The final implementation type of {@link ClickableBuilder}.
 */
public interface ClickableBuilder<C extends Component & ClickNotifier<C>, B extends ClickableBuilder<C, B>> extends EntityBuilder<C, B> {

    /**
     * Builder method, configures a {@link ComponentEventListener} for the {@link Component}'s {@link ClickEvent}s.
     *
     * @see ClickNotifier#addClickListener(ComponentEventListener)
     * @param listener The listener to add; might <b>not</b> be null.
     * @return this
     */
    default B addClickListener(ComponentEventListener<ClickEvent<C>> listener) {
        return configure(clickable -> clickable.addClickListener(listener));
    }

    /**
     * Builder method, configures a shortcut for a click on the {@link Component}.
     *
     * @see ClickNotifier#addClickShortcut(Key, KeyModifier...)
     * @param key The {@link Key} to add the shortcut for; might <b>not</b> be null.
     * @param keyModifiers The additional {@link KeyModifier}s; might <b>not</b> be null.
     * @return this
     */
    default B addClickShortcut(Key key, KeyModifier... keyModifiers) {
        return configure(clickable -> clickable.addClickShortcut(key, keyModifiers));
    }
}
