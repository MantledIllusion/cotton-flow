package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.mantledillusion.vaadin.cotton.component.css.CssStyle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;

/**
 * {@link EntityBuilder} for {@link HasElement} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasElement}.
 * @param <B>
 *            The final implementation type of {@link HasElementBuilder}.
 */
public interface HasElementBuilder<C extends HasElement, B extends HasElementBuilder<C, B>> extends EntityBuilder<C, B> {

    /**
     * Builder method, configures one or more style class names to be added.
     *
     * @see com.vaadin.flow.dom.Element#setAttribute(String, String)
     * @param msgId
     *            The tooltip text, or a message id to localize; might be null.
     * @return this
     */
    default B setNativeTooltip(String msgId) {
        return configure(hasElement -> hasElement.getElement().setAttribute("title", WebEnv.getTranslation(msgId)));
    }

    /**
     * Builder method, configures whether the {@link HasElement} is visible.
     *
     * @see com.vaadin.flow.dom.Element#setVisible(boolean)
     * @param visible
     *            True if the {@link HasElement} should be invisible, false otherwise.
     * @return this
     */
    default B setVisible(boolean visible) {
        return configure(hasElement -> hasElement.getElement().setVisible(visible));
    }

    /**
     * Builder method, configures a specific value for a CSS style.
     *
     * @see com.vaadin.flow.dom.Style#set(String, String)
     * @param name
     *            The first style property name to add; might <b>not</b> be null.
     * @param value
     *            The first style value to set; might be null.
     * @return this
     */
    default B setCssStyle(String name, String value) {
        return configure(hasElement -> hasElement.getElement().getStyle().set(name, value));
    }

    /**
     * Builder method, configures a specific value for a CSS style.
     *
     * @see com.vaadin.flow.dom.Style#set(String, String)
     * @param style
     *            The style to add; might <b>not</b> be null.
     * @return this
     */
    default B setCssStyle(CssStyle style) {
        return configure(style::apply);
    }
}
