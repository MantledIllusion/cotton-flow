package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * {@link ComponentBuilder} for {@link MenuBar}s.
 */
public class MenuBarBuilder extends AbstractComponentBuilder<MenuBar, MenuBarBuilder> implements
        HasElementBuilder<MenuBar, MenuBarBuilder>,
        HasSizeBuilder<MenuBar, MenuBarBuilder>,
        HasThemeVariantBuilder<MenuBar, MenuBarBuilder, MenuBarVariant>,
        HasStyleBuilder<MenuBar, MenuBarBuilder> {

    /**
     * {@link ComponentBuilder} for {@link MenuItem}s.
     */
    public static class MenuItemBuilder<PC, PB> extends AbstractEntityBuilder<MenuItem, MenuItemBuilder<PC, PB>> implements
            Configurer<PC>,
            RegistrationBuilder<MenuItem, MenuItemBuilder<PC, PB>>,
            HasEnabledBuilder<MenuItem, MenuItemBuilder<PC, PB>>,
            HasComponentsBuilder<MenuItem, MenuItemBuilder<PC, PB>>,
            ClickNotifierBuilder<MenuItem, MenuItemBuilder<PC, PB>> {

        private final PB parentBuilder;
        private final Function<PC, MenuItem> itemSupplier;

        private MenuItemBuilder(PB parentBuilder, Function<PC, MenuItem> itemSupplier) {
            this.parentBuilder = parentBuilder;
            this.itemSupplier = itemSupplier;
        }

        @Override
        public void configure(PC component) {
            apply(this.itemSupplier.apply(component));
        }

        /**
         * Builder method, configures the {@link MenuItem} to be toggleable.
         *
         * @see MenuItem#setCheckable(boolean)
         * @param checkable
         *            True if the item should be toggleable, false otherwise.
         * @return this
         */
        public MenuItemBuilder<PC, PB> setCheckable(boolean checkable) {
            return configure(menuItem -> menuItem.setCheckable(checkable));
        }

        /**
         * Builder method, configures the {@link MenuItem} to be toggled on.
         * <p>
         * Requires {@link #setCheckable(boolean)} to be called with true.
         *
         * @see MenuItem#setChecked(boolean)
         * @param checked
         *            True if the item should be toggled on, false otherwise.
         * @return this
         */
        public MenuItemBuilder<PC, PB> setChecked(boolean checked) {
            return configure(menuItem -> menuItem.setChecked(checked));
        }

        /**
         * Builder method, configures a new listener for {@link ClickEvent}s.
         *
         * @see MenuItem#addClickListener(ComponentEventListener)
         * @param listener
         *            The listener to add; might <b>not</b> be null.
         * @return this
         */
        public MenuItemBuilder<PC, PB> addClickListener(ComponentEventListener<ClickEvent<MenuItem>> listener) {
            return configure(menuItem -> menuItem.addClickListener(listener));
        }

        /**
         * Builder method, configures a key combination to trigger a {@link ClickEvent}.
         *
         * @see MenuItem#addClickShortcut(Key, KeyModifier...)
         * @param key
         *            The key to react to; might <b>not</b> be null.
         * @param keyModifiers
         *            The modifiers that have to be applied; might be null.
         * @return this
         */
        public MenuItemBuilder<PC, PB> addClickShortcut(Key key, KeyModifier... keyModifiers) {
            return configure(menuItem -> menuItem.addClickShortcut(key, keyModifiers));
        }

        /**
         * Builder method, configures a new {@link MenuItem}.
         *
         * @see com.vaadin.flow.component.contextmenu.SubMenu#addItem(String)
         * @param msgId
         *            The text to use on the item, or a message id to localize; might be null.
         * @return A new {@link MenuItemBuilder}, never null
         */
        public MenuItemBuilder<MenuItem, MenuItemBuilder<PC, PB>> configureItem(String msgId) {
            MenuItemBuilder<MenuItem, MenuItemBuilder<PC, PB>> itemBuilder = new MenuItemBuilder<>(this,
                    menuItem -> menuItem.getSubMenu().addItem(WebEnv.getTranslation(msgId)));
            configure(itemBuilder);
            return itemBuilder;
        }

        /**
         * Builder method, configures a new {@link MenuItem}.
         *
         * @see com.vaadin.flow.component.contextmenu.SubMenu#addItem(Component)
         * @param component
         *            The MenuItemBuilder to use as the item; might <b>not</b> be null.
         * @return A new {@link MenuItemBuilder}, never null
         */
        public MenuItemBuilder<MenuItem, MenuItemBuilder<PC, PB>> configureItem(Component component) {
            MenuItemBuilder<MenuItem, MenuItemBuilder<PC, PB>> itemBuilder = new MenuItemBuilder<>(this,
                    menuItem -> menuItem.getSubMenu().addItem(component));
            configure(itemBuilder);
            return itemBuilder;
        }

        public PB add() {
            return this.parentBuilder;
        }

        /**
         * Adds the currently configured menu item to the {@link MenuBar} being build by the returned
         * {@link MenuBarBuilder} and uses the given {@link ModelAccessor} to bind the {@link MenuItem} to the
         * given {@link Property}.
         *
         * @param <ModelType>
         *            The type of the model to whose property to bind.
         * @param binder
         *            The {@link ModelAccessor} to bind the {@link MenuItem} with; might <b>not</b> be null.
         * @param property
         *            The {@link Property} to bind the {@link MenuItem} to; might <b>not</b> be null.
         * @return The {@link MenuBarBuilder} that started this {@link MenuBarBuilder.MenuItemBuilder}, never null
         */
        public <ModelType, ValueType> PB add(ModelAccessor<ModelType> binder,
                                             Property<ModelType, ValueType> property,
                                             BiConsumer<MenuItem, ValueType> valueSetter) {
            if (binder == null) {
                throw new Http901IllegalArgumentException("Cannot bind using a null binder.");
            } else if (property == null) {
                throw new Http901IllegalArgumentException("Cannot bind using a null property.");
            } else if (valueSetter == null) {
                throw new Http901IllegalArgumentException("Cannot bind using a null valueSetter.");
            }
            configure(menuItem -> binder.bindConsumer(valueType -> valueSetter.accept(menuItem, valueType), property));
            return this.parentBuilder;
        }
    }

    private MenuBarBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static MenuBarBuilder create() {
        return new MenuBarBuilder();
    }

    @Override
    protected MenuBar instantiate() {
        return new MenuBar();
    }

    @Override
    public String toVariantName(MenuBarVariant variant) {
        return variant.getVariantName();
    }

    /**
     * Builder method, configures if items with sub-items of the {@link MenuBar} should open upon mouse hover.
     *
     * @see MenuBar#setOpenOnHover(boolean)
     * @param openOnHover
     *            True if the items should open on hover, false otherwise.
     * @return this
     */
    public MenuBarBuilder setOpenOnHover(boolean openOnHover) {
        return configure(menuBar -> menuBar.setOpenOnHover(openOnHover));
    }

    /**
     * Builder method, configures a new {@link MenuItem}.
     *
     * @see MenuBar#addItem(String)
     * @param msgId
     *            The text to use on the item, or a message id to localize; might be null.
     * @return A new {@link MenuItemBuilder}, never null
     */
    public MenuItemBuilder<MenuBar, MenuBarBuilder> configureItem(String msgId) {
        MenuItemBuilder<MenuBar, MenuBarBuilder> itemBuilder = new MenuItemBuilder<>(this,
                bar -> bar.addItem(WebEnv.getTranslation(msgId)));
        configure(itemBuilder);
        return itemBuilder;
    }

    /**
     * Builder method, configures a new {@link MenuItem}.
     *
     * @see MenuBar#addItem(String)
     * @param component
     *            The MenuItemBuilder to use as the item; might <b>not</b> be null.
     * @return A new {@link MenuItemBuilder}, never null
     */
    public MenuItemBuilder<MenuBar, MenuBarBuilder> configureItem(Component component) {
        MenuItemBuilder<MenuBar, MenuBarBuilder> itemBuilder = new MenuItemBuilder<>(this,
                bar -> bar.addItem(component));
        configure(itemBuilder);
        return itemBuilder;
    }
}
