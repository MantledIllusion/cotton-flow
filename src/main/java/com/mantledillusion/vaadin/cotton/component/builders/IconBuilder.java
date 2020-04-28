package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.ClickableBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasElementBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasStyleBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Locale;

/**
 * {@link ComponentBuilder} for {@link Icon}s.
 */
public class IconBuilder extends AbstractComponentBuilder<Icon, IconBuilder> implements
        HasElementBuilder<Icon, IconBuilder>,
        HasStyleBuilder<Icon, IconBuilder>,
        ClickableBuilder<Icon, IconBuilder> {

    private String collection;
    private String icon;

    private IconBuilder() {
        setIcon(VaadinIcon.VAADIN_H);
    }

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static IconBuilder create() {
        return new IconBuilder();
    }

    @Override
    protected Icon instantiate() {
        return new Icon();
    }

    /**
     * Builder method, configures the {@link Image}'s source as an image available over the web.
     *
     * @see Icon#Icon(VaadinIcon)
     * @param vaadinIcon
     *            The {@link VaadinIcon} to use; might <b>not</b> be null.
     * @return this
     */
    public IconBuilder setIcon(VaadinIcon vaadinIcon) {
        if (vaadinIcon == null) {
            throw new Http901IllegalArgumentException("Cannot create icon for a null vaadin icon");
        }
        return setIcon("vaadin", vaadinIcon.name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
    }

    /**
     * Builder method, configures the {@link Image}'s source as an image available over the web.
     *
     * @see Icon#Icon(String, String)
     * @param collection
     *            The collection of the image; might <b>not</b> be null.
     * @param icon
     *            The name of the image; might <b>not</b> be null.
     * @return this
     */
    public IconBuilder setIcon(String collection, String icon) {
        if (collection == null) {
            throw new Http901IllegalArgumentException("Cannot create icon for a null collection");
        } else if (icon == null) {
            throw new Http901IllegalArgumentException("Cannot create icon for a null icon");
        }
        this.collection = collection;
        this.icon = icon;
        return this;
    }
}
