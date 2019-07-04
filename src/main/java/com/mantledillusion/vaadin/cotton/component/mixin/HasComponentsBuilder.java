package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

/**
 * {@link ComponentBuilder} for {@link HasComponents} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasComponents}.
 * @param <B>
 *            The final implementation type of {@link HasComponentsBuilder}.
 */
public interface HasComponentsBuilder<C extends HasComponents, B extends HasComponentsBuilder<C, B>> extends ComponentBuilder<C, B> {

    /**
     * Builder method, configures the {@link Component}'s child {@link Component}s.
     *
     * @see HasComponents#add(Component...)
     * @return this
     */
    default B add(Component... components) {
        return configure(hasComponents -> hasComponents.add(components));
    }

    /**
     * Builder method, configures the {@link Component}'s nth child {@link Component}.
     *
     * @see HasComponents#addComponentAtIndex(int, Component)
     * @return this
     */
    default B addComponentAtIndex(int index, Component component) {
        return configure(hasComponents -> hasComponents.addComponentAtIndex(index, component));
    }
}
