package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;

/**
 * {@link EntityBuilder} for {@link ThemableLayout} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link ThemableLayout}.
 * @param <B>
 *            The final implementation type of {@link ThemableLayoutBuilder}.
 */
public interface ThemableLayoutBuilder<C extends ThemableLayout, B extends ThemableLayoutBuilder<C, B>> extends EntityBuilder<C, B> {

    /**
     * Builder method, configures the {@link Component} to be margined.
     *
     * @see ThemableLayout#setMargin(boolean)
     * @param margin True if there should be a margin, false otherwise.
     * @return this
     */
    default B setMargin(boolean margin) {
        return configure(themable -> themable.setMargin(margin));
    }

    /**
     * Builder method, configures the {@link Component} to be padded.
     *
     * @see ThemableLayout#setPadding(boolean)
     * @param padding True if there should be padding, false otherwise.
     * @return this
     */
    default B setPadding(boolean padding) {
        return configure(themable -> themable.setPadding(padding));
    }

    /**
     * Builder method, configures the {@link Component}'s inner component to be spaced from each other.
     *
     * @see ThemableLayout#setSpacing(boolean)
     * @param spacing True if there should be spacing, false otherwise.
     * @return this
     */
    default B setSpacing(boolean spacing) {
        return configure(themable -> themable.setSpacing(spacing));
    }

    /**
     * Builder method, configures the {@link Component}'s box sizing.
     *
     * @see ThemableLayout#setBoxSizing(BoxSizing)
     * @param boxSizing The {@link BoxSizing} of the layout; might be null, interpreted as {@link BoxSizing#UNDEFINED}.
     * @return this
     */
    default B setBoxSizing(BoxSizing boxSizing) {
        return configure(themable -> themable.setBoxSizing(boxSizing));
    }
}
