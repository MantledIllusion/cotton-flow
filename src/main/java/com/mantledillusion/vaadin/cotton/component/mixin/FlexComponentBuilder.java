package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

/**
 * {@link ComponentBuilder} for {@link FlexComponent} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link FlexComponent}.
 * @param <B>
 *            The final implementation type of {@link FlexComponentBuilder}.
 */
public interface FlexComponentBuilder<C extends FlexComponent, B extends FlexComponentBuilder<C, B>> extends ComponentBuilder<C, B> {

    /**
     * Builder method, configures the {@link Component}'s item alignment.
     *
     * @see FlexComponent#setAlignItems(FlexComponent.Alignment)
     * @return this
     */
    default B setAlignItems(FlexComponent.Alignment alignment) {
        return configure(flex -> flex.setAlignItems(alignment));
    }

    /**
     * Builder method, configures the {@link Component}'s own alignment.
     *
     * @see FlexComponent#setAlignSelf(FlexComponent.Alignment, HasElement...)
     * @return this
     */
    default B setAlignSelf(FlexComponent.Alignment alignment, HasElement... elementContainers) {
        return configure(flex -> flex.setAlignSelf(alignment, elementContainers));
    }

    /**
     * Builder method, configures the {@link Component}'s flex grow.
     *
     * @see FlexComponent#setFlexGrow(double, HasElement...)
     * @return this
     */
    default B setFlexGrow(double flexGrow, HasElement... elementContainers) {
        return configure(flex -> flex.setFlexGrow(flexGrow, elementContainers));
    }

    /**
     * Builder method, configures the {@link Component}'s {@link FlexComponent.JustifyContentMode}.
     *
     * @see FlexComponent#setJustifyContentMode(FlexComponent.JustifyContentMode)
     * @return this
     */
    default B setJustifyContentMode(FlexComponent.JustifyContentMode justifyContentMode) {
        return configure(flex -> flex.setJustifyContentMode(justifyContentMode));
    }
}