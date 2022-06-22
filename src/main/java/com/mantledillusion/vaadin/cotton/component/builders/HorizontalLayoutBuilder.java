package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * {@link ComponentBuilder} for {@link HorizontalLayout}s.
 */
public class HorizontalLayoutBuilder extends AbstractComponentBuilder<HorizontalLayout, HorizontalLayoutBuilder> implements
        HasElementBuilder<HorizontalLayout, HorizontalLayoutBuilder>,
        HasSizeBuilder<HorizontalLayout, HorizontalLayoutBuilder>,
        HasStyleBuilder<HorizontalLayout, HorizontalLayoutBuilder>,
        HasEnabledBuilder<HorizontalLayout, HorizontalLayoutBuilder>,
        HasComponentsBuilder<HorizontalLayout, HorizontalLayoutBuilder>,
        ThemableLayoutBuilder<HorizontalLayout, HorizontalLayoutBuilder>,
        FlexComponentBuilder<HorizontalLayout, HorizontalLayoutBuilder>,
        ClickNotifierBuilder<HorizontalLayout, HorizontalLayoutBuilder> {

    private HorizontalLayoutBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static HorizontalLayoutBuilder create() {
        return new HorizontalLayoutBuilder();
    }

    @Override
    protected HorizontalLayout instantiate() {
        return new HorizontalLayout();
    }

    /**
     * Builder method, configures the {@link Component}'s vertical child {@link Component} alignment.
     *
     * @see HorizontalLayout#setVerticalComponentAlignment(FlexComponent.Alignment, Component...)
     * @param alignment
     *            the individual alignment for the children components. Setting
     *            <code>null</code> will reset the alignment to its default
     * @param componentsToAlign
     *            The components to which the individual alignment should be set
     * @return this
     */
    public HorizontalLayoutBuilder setVerticalComponentAlignment(FlexComponent.Alignment alignment,
                                                                   Component... componentsToAlign) {
        return configure(horizontalLayout -> horizontalLayout.setVerticalComponentAlignment(alignment, componentsToAlign));
    }

    /**
     * Builder method, configures the {@link Component}'s default vertical child {@link Component} alignment.
     *
     * @see HorizontalLayout#setDefaultVerticalComponentAlignment(FlexComponent.Alignment)
     * @param alignment
     *            the alignment to apply to the components. Setting
     *            <code>null</code> will reset the alignment to its default
     * @return this
     */
    public HorizontalLayoutBuilder setDefaultVerticalComponentAlignment(FlexComponent.Alignment alignment) {
        return configure(horizontalLayout -> horizontalLayout.setDefaultVerticalComponentAlignment(alignment));
    }

    /**
     * Builder method, configures the {@link Component}'s child {@link Component}s.
     *
     * @see HorizontalLayout#add(Component...)
     * @see HorizontalLayout#setVerticalComponentAlignment(FlexComponent.Alignment, Component...)
     * @param alignment The alignment to align the given components with; might <b>not</b> be null.
     * @param components The {@link Component}s to add; might be null, might <b>not</b> contain nulls.
     * @return this
     */
    public HorizontalLayoutBuilder add(FlexComponent.Alignment alignment, Component... components) {
        return configure(verticalLayout -> {
            verticalLayout.add(components);
            verticalLayout.setVerticalComponentAlignment(alignment, components);
        });
    }
}
