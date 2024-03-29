package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

/**
 * {@link ComponentBuilder} for {@link FlexLayout}s.
 */
public class FlexLayoutBuilder extends AbstractComponentBuilder<FlexLayout, FlexLayoutBuilder> implements
        HasElementBuilder<FlexLayout, FlexLayoutBuilder>,
        HasSizeBuilder<FlexLayout, FlexLayoutBuilder>,
        HasStyleBuilder<FlexLayout, FlexLayoutBuilder>,
        HasEnabledBuilder<FlexLayout, FlexLayoutBuilder>,
        HasComponentsBuilder<FlexLayout, FlexLayoutBuilder>,
        FlexComponentBuilder<FlexLayout, FlexLayoutBuilder>,
        ClickNotifierBuilder<FlexLayout, FlexLayoutBuilder> {

    private FlexLayoutBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static FlexLayoutBuilder create() {
        return new FlexLayoutBuilder();
    }

    @Override
    protected FlexLayout instantiate() {
        return new FlexLayout();
    }

    /**
     * Builder method, configures the {@link Component}'s {@link FlexLayout.FlexWrap}.
     *
     * @see FlexLayout#setFlexWrap(FlexLayout.FlexWrap)
     * @param wrapMode the flex wrap mode of the layout, never <code>null</code>
     * @return this
     */
    public FlexLayoutBuilder setFlexWrap(FlexLayout.FlexWrap wrapMode) {
        return configure(flexLayout -> flexLayout.setFlexWrap(wrapMode));
    }
}
