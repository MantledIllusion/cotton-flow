package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.function.BiConsumer;

/**
 * {@link ComponentBuilder} for {@link Tabs}.
 */
public class TabsBuilder extends AbstractComponentBuilder<Tabs, TabsBuilder> implements
        HasElementBuilder<Tabs, TabsBuilder>,
        HasSizeBuilder<Tabs, TabsBuilder>,
        HasThemeVariantBuilder<Tabs, TabsBuilder, TabsVariant>,
        HasStyleBuilder<Tabs, TabsBuilder>,
        HasEnabledBuilder<Tabs, TabsBuilder>,
        HasComponentsBuilder<Tabs, TabsBuilder> {

    /**
     * {@link ComponentBuilder} for {@link Tab}s.
     */
    public class TabBuilder extends AbstractEntityBuilder<Tab, TabBuilder> implements
            Configurer<Tabs>,
            HasElementBuilder<Tab, TabBuilder>,
            HasThemeVariantBuilder<Tab, TabBuilder, TabVariant>,
            HasStyleBuilder<Tab, TabBuilder>,
            HasEnabledBuilder<Tab, TabBuilder>,
            HasComponentsBuilder<Tab, TabBuilder> {

        private TabBuilder() {
            super(TabsBuilder.this);
        }

        @Override
        public void configure(Tabs component) {
            Tab tab = new Tab();
            component.add(tab);
            apply(tab);
        }

        @Override
        public String toVariantName(TabVariant variant) {
            return variant.getVariantName();
        }

        /**
         * Builder method, configures the {@link Tab}'s label.
         *
         * @see Tab#setLabel(String)
         * @param msgId
         *            The label text, or a message id to localize; might be null.
         * @return this
         */
        public TabBuilder setLabel(String msgId) {
            return configure(tab -> tab.setLabel(WebEnv.getTranslation(msgId)));
        }

        /**
         * Builder method, configures the {@link Tab}'s flex grow.
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
         * Builder method, configures the {@link Tab} to be selected.
         *
         * @see Tab#setSelected(boolean)
         * @param selected
         *            the boolean value to set
         * @return this
         */
        public TabBuilder setSelected(boolean selected) {
            return configure(tab -> {
                tab.setSelected(selected);
                if (selected) {
                    TabBuilder.this.get(Tabs.class).setSelectedTab(tab);
                }
            });
        }

        /**
         * Builder method, configures a new {@link ComponentEventListener} for {@link Tabs.SelectedChangeEvent}s when a
         * the tab that is currently being configured {@link Tab} is selected.
         *
         * @see Tabs#addSelectedChangeListener(ComponentEventListener)
         * @param listener The listener to add; might <b>not</b> be null.
         * @return this
         */
        public TabBuilder addSelectedChangeListenerForTab(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
            return configure(tab -> TabBuilder.this.get(Tabs.class).addSelectedChangeListener(event -> {
                if (tab == event.getSelectedTab()) {
                    listener.onComponentEvent(event);
                }
            }));
        }

        /**
         * Builder method, configures the {@link Tab}'s enablement.
         *
         * @see Tab#setVisible(boolean)
         * @param enabled
         *          True if the {@link Tab} should be enabled, false otherwise.
         * @return this
         */
        public TabBuilder setEnabled(boolean enabled) {
            return configure(tab -> tab.setEnabled(enabled));
        }

        /**
         * Builder method, configures the {@link Tab}'s visibility.
         *
         * @see Tab#setVisible(boolean)
         * @param visible
         *          True if the {@link Tab} should be invisible, false otherwise.
         * @return this
         */
        public TabBuilder setVisible(boolean visible) {
            return configure(tab -> tab.setVisible(visible));
        }

        /**
         * Adds the currently configured tab to the {@link Tabs} being build by the returned {@link TabsBuilder}.
         *
         * @return The {@link TabsBuilder} that started this {@link TabsBuilder.TabBuilder}, never null
         */
        public TabsBuilder add() {
            return TabsBuilder.this;
        }

        /**
         * Adds the currently configured tab to the {@link Tabs} being build by the returned {@link TabsBuilder} and
         * uses the given {@link ModelAccessor} to bind the {@link Tab} to the given {@link Property}.
         *
         * @param <ModelType>
         *            The type of the model to whose property to bind.
         * @param binder
         *            The {@link ModelAccessor} to bind the {@link Tab} with; might <b>not</b> be null.
         * @param property
         *            The {@link Property} to bind the {@link Tab} to; might <b>not</b> be null.
         * @return The {@link TabsBuilder} that started this {@link TabsBuilder.TabBuilder}, never null
         */
        public <ModelType, ValueType> TabsBuilder add(ModelAccessor<ModelType> binder,
                                                      Property<ModelType, ValueType> property,
                                                      BiConsumer<Tab, ValueType> valueSetter) {
            if (binder == null) {
                throw new Http901IllegalArgumentException("Cannot bind using a null binder.");
            } else if (property == null) {
                throw new Http901IllegalArgumentException("Cannot bind using a null property.");
            } else if (valueSetter == null) {
                throw new Http901IllegalArgumentException("Cannot bind using a null valueSetter.");
            }
            configure(tab -> binder.bindConsumer(valueType -> valueSetter.accept(tab, valueType), property));
            return TabsBuilder.this;
        }
    }

    private TabsBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static TabsBuilder create() {
        return new TabsBuilder();
    }

    @Override
    protected Tabs instantiate() {
        return new Tabs();
    }

    @Override
    public String toVariantName(TabsVariant variant) {
        return variant.getVariantName();
    }

    /**
     * Builder method, configures the {@link Component}'s {@link Tab}s.
     *
     * @see Tabs#add(Tab...)
     * @param tabs The tabs to enclose; might be null, might <b>not</b> contain nulls.
     * @return this
     */
    public TabsBuilder add(Tab... tabs) {
        return configure(tabsComponent -> tabsComponent.add(tabs));
    }

    /**
     * Builder method, configures a new {@link Tab}.
     *
     * @see Tabs#add(Tab...)
     * @return A new {@link TabBuilder}, never null
     */
    public TabBuilder configureTab() {
        TabBuilder tabBuilder = new TabBuilder();
        configure(tabBuilder);
        return tabBuilder;
    }

    /**
     * Builder method, configures a new {@link Tab}.
     *
     * @see Tabs#add(Tab...)
     * @param content The {@link Component} the new {@link Tab} is being configured for; will automatically call
     *                {@link Component#setVisible(boolean)} depending on whether the {@link Tab} is being (de)selected.
     * @return A new {@link TabBuilder}, never null
     */
    public TabBuilder configureTab(Component content) {
        TabBuilder tabBuilder = new TabBuilder();
        configure(tabBuilder);
        tabBuilder.configure(tab -> get(Tabs.class).addSelectedChangeListener(event -> content.setVisible(event.getSelectedTab() == tab)));
        return tabBuilder;
    }

    /**
     * Builder method, configures the {@link Component}'s selected {@link Tab}.
     *
     * @see Tabs#setSelectedIndex(int)
     * @param selectedIndex
     *            the zero-based index of the selected tab, -1 to unselect all
     * @return this
     */
    public TabsBuilder setSelectedIndex(int selectedIndex) {
        return configure(tabsComponent -> tabsComponent.setSelectedIndex(selectedIndex));
    }

    /**
     * Builder method, configures the {@link Component}'s selected {@link Tab}.
     *
     * @see Tabs#setSelectedTab(Tab)
     * @param selectedTab
     *            the tab to select, {@code null} to unselect all
     * @return this
     */
    public TabsBuilder setSelectedTab(Tab selectedTab) {
        return configure(tabsComponent -> tabsComponent.setSelectedTab(selectedTab));
    }

    /**
     * Builder method, configures the {@link Component}'s {@link Tabs.Orientation}.
     *
     * @see Tabs#setOrientation(Tabs.Orientation)
     * @param orientation
     *            the orientation
     * @return this
     */
    public TabsBuilder setOrientation(Tabs.Orientation orientation) {
        return configure(tabsComponent -> tabsComponent.setOrientation(orientation));
    }

    /**
     * Builder method, configures the {@link Component}'s flex grow for enclosed {@link Tab}s.
     *
     * @see Tabs#setFlexGrowForEnclosedTabs(double)
     * @param flexGrow
     *            the proportion of the available space the enclosed tabs should
     *            take up
     * @return this
     */
    public TabsBuilder setFlexGrowForEnclosedTabs(double flexGrow) {
        return configure(tabsComponent -> tabsComponent.setFlexGrowForEnclosedTabs(flexGrow));
    }

    /**
     * Builder method, configures a new {@link ComponentEventListener} for {@link Tabs.SelectedChangeEvent}s when a
     * different {@link Tab} is selected.
     *
     * @see Tabs#addSelectedChangeListener(ComponentEventListener)
     * @param listener The listener to add; might <b>not</b> be null.
     * @return this
     */
    public TabsBuilder addSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        return configure(tabsComponent -> tabsComponent.addSelectedChangeListener(listener));
    }
}
