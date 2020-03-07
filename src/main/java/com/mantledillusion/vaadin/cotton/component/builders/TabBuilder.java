package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasComponentsBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasEnabledBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasStyleBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;

/**
 * {@link ComponentBuilder} for {@link Tab}s.
 */
public class TabBuilder extends AbstractComponentBuilder<Tab, TabBuilder>
        implements HasStyleBuilder<Tab, TabBuilder>, HasEnabledBuilder<Tab, TabBuilder>,
        HasComponentsBuilder<Tab, TabBuilder> {

    @Override
    protected Tab instantiate() {
        return new Tab();
    }

    /**
     * Builder method, configures the {@link Component}'s label.
     *
     * @see Tab#setLabel(String)
     * @param label
     *            the label to display
     * @return this
     */
    public TabBuilder setLabel(String label) {
        return configure(tab -> tab.setLabel(label));
    }

    /**
     * Builder method, configures the {@link Component}'s flex grow.
     *
     * @see Tab#setFlexGrow(double)
     * @param flexGrow
     *            the proportion of the available space the tab should take up
     * @return this
     */
    public TabBuilder setFlexGrow(double flexGrow) {
        return configure(tab -> tab.setFlexGrow(flexGrow));
    }

    /**
     * Builder method, configures the {@link Component} to be selected.
     *
     * @see Tab#setSelected(boolean)
     * @param selected
     *            the boolean value to set
     * @return this
     */
    public TabBuilder setSelected(boolean selected) {
        return configure(tab -> tab.setSelected(selected));
    }
}
