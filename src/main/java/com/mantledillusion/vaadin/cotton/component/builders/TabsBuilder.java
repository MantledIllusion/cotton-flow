package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

/**
 * {@link ComponentBuilder} for {@link Tabs}.
 */
public class TabsBuilder extends AbstractComponentBuilder<Tabs, TabsBuilder>
        implements HasSizeBuilder<Tabs, TabsBuilder>,
        HasStyleBuilder<Tabs, TabsBuilder>,
        HasEnabledBuilder<Tabs, TabsBuilder>,
        HasComponentsBuilder<Tabs, TabsBuilder> {

    /**
     * {@link ComponentBuilder} for {@link Tab}s.
     */
    public final class TabBuilder extends AbstractEntityBuilder<Tab, TabBuilder> implements Configurer<Tabs>,
            HasStyleBuilder<Tab, TabBuilder>, HasEnabledBuilder<Tab, TabBuilder>, HasComponentsBuilder<Tab, TabBuilder> {

        private TabBuilder() {
            super(TabsBuilder.this);
        }

        @Override
        public void configure(Tabs component) {
            Tab tab = new Tab();
            component.add(tab);
            apply(tab);
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

        /**
         * Builder method, configures a new {@link ComponentEventListener} for {@link Tabs.SelectedChangeEvent}s when a
         * the tab that is currently being configured {@link Tab} is selected.
         *
         * @see Tabs#addSelectedChangeListener(ComponentEventListener)
         * @param listener The listener to add; might <b>not</b> be null.
         * @return this
         */
        public TabBuilder addSelectedChangeListenerForTab(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
            return configure(tab -> TabsBuilder.this.addSelectedChangeListener(event -> {
                if (tab == event.getSelectedTab()) {
                    listener.onComponentEvent(event);
                }
            }));
        }

        /**
         * Adds the currently configured tab to the {@link Tabs} being build by the returned {@link TabsBuilder}.
         *
         * @return The {@link TabsBuilder} that started this {@link TabsBuilder.TabBuilder}, never null
         */
        public TabsBuilder add() {
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
