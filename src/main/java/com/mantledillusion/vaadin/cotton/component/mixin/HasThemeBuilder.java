package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;

/**
 * {@link EntityBuilder} for {@link HasTheme} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasTheme}.
 * @param <B>
 *            The final implementation type of {@link HasThemeBuilder}.
 */
public interface HasThemeBuilder<C extends HasTheme, B extends HasThemeBuilder<C, B>> extends EntityBuilder<C, B> {

    /**
     * Builder method, add the given theme name to the {@link Component}.
     *
     * @see HasTheme#addThemeName(String)
     * @param themeName The theme name to add; might <b>not</b> be null.
     * @return this
     */
    default B addThemeName(String themeName) {
        return configure(hasTheme -> hasTheme.addThemeName(themeName));
    }

    /**
     * Builder method, removes the given theme name from the {@link Component}.
     *
     * @see HasTheme#removeThemeName(String)
     * @param themeName The theme name to remove; might <b>not</b> be null.
     * @return this
     */
    default B removeThemeName(String themeName) {
        return configure(hasTheme -> hasTheme.removeThemeName(themeName));
    }

    /**
     * Builder method, sets the given theme name from the {@link Component}.
     *
     * @see HasTheme#setThemeName(String)
     * @param themeName The theme name to set; might <b>not</b> be null.
     * @return this
     */
    default B setThemeName(String themeName) {
        return configure(hasTheme -> hasTheme.setThemeName(themeName));
    }
}
