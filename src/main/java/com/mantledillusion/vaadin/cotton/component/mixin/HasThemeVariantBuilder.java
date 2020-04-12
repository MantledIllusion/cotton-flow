package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;

/**
 * {@link EntityBuilder} for {@link HasTheme} implementing {@link Component}s that also has an {@link Enum} containing
 * theme variants.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasTheme}.
 * @param <B>
 *            The final implementation type of {@link HasThemeVariantBuilder}.
 */
public interface HasThemeVariantBuilder<C extends HasTheme, B extends HasThemeVariantBuilder<C, B, V>, V extends Enum<V>> extends HasThemeBuilder<C, B> {

    /**
     * Converts the given variant {@link Enum} value to its theme name.
     *
     * @param variant The variant to convert; might <b>not</b> be null.
     * @return The variant theme's name, never null
     */
    String toVariantName(V variant);

    /**
     * Builder method, adds the theme name of the given variant to the {@link Component}.
     *
     * @param variant The variant whose theme name to add; might <b>not</b> be null.
     * @return this
     */
    default B addThemeVariant(V variant) {
        return configure(hasTheme -> hasTheme.getThemeNames().add(toVariantName(variant)));
    }

    /**
     * Builder method, removes the theme name of the given variant from the {@link Component}.
     *
     * @param variant The variant whose theme name to remove; might <b>not</b> be null.
     * @return this
     */
    default B removeThemeVariant(V variant) {
        return configure(hasTheme -> hasTheme.getThemeNames().remove(toVariantName(variant)));
    }
}
