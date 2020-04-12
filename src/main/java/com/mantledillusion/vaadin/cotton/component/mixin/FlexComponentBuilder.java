package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

/**
 * {@link EntityBuilder} for {@link FlexComponent} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link FlexComponent}.
 * @param <B>
 *            The final implementation type of {@link FlexComponentBuilder}.
 */
public interface FlexComponentBuilder<C extends FlexComponent, B extends FlexComponentBuilder<C, B>> extends EntityBuilder<C, B> {

    /**
     * Builder method, configures the {@link Component}'s item alignment.
     *
     * @see FlexComponent#setAlignItems(FlexComponent.Alignment)
     * @param alignment The {@link FlexComponent.Alignment} to align all {@link Component}s to; might <b>not</b> be null.
     * @return this
     */
    default B setAlignItems(FlexComponent.Alignment alignment) {
        return configure(flex -> flex.setAlignItems(alignment));
    }

    /**
     * Builder method, configures the {@link Component}'s own alignment.
     *
     * @see FlexComponent#setAlignSelf(FlexComponent.Alignment, HasElement...)
     * @param alignment The {@link FlexComponent.Alignment} to align {@link Component}s to; might <b>not</b> be null.
     * @param elementContainers The {@link HasElement}s to align; might be null, might <b>not</b> contain nulls.
     * @return this
     */
    default B setAlignSelf(FlexComponent.Alignment alignment, HasElement... elementContainers) {
        return configure(flex -> flex.setAlignSelf(alignment, elementContainers));
    }

    /**
     * Builder method, configures the {@link Component}'s flex grow.
     *
     * @see FlexComponent#setFlexGrow(double, HasElement...)
     * @param flexGrow The proportion of the available space the element container should take up.
     * @param elementContainers The {@link HasElement}s to align; might be null, might <b>not</b> contain nulls.
     * @return this
     */
    default B setFlexGrow(double flexGrow, HasElement... elementContainers) {
        return configure(flex -> flex.setFlexGrow(flexGrow, elementContainers));
    }

    /**
     * Builder method, configures the {@link Component}'s {@link FlexComponent.JustifyContentMode}.
     *
     * @see FlexComponent#setJustifyContentMode(FlexComponent.JustifyContentMode)
     * @param justifyContentMode The {@link FlexComponent.JustifyContentMode} of the layout; might <b>not</b> be null.
     * @return this
     */
    default B setJustifyContentMode(FlexComponent.JustifyContentMode justifyContentMode) {
        return configure(flex -> flex.setJustifyContentMode(justifyContentMode));
    }
}