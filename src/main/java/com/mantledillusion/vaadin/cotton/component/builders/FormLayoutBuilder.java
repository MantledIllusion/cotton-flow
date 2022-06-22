package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;

import java.util.function.Function;

/**
 * {@link ComponentBuilder} for {@link FormLayout}s.
 */
public class FormLayoutBuilder extends AbstractComponentBuilder<FormLayout, FormLayoutBuilder> implements
        HasElementBuilder<FormLayout, FormLayoutBuilder>,
        HasSizeBuilder<FormLayout, FormLayoutBuilder>,
        HasStyleBuilder<FormLayout, FormLayoutBuilder>,
        HasEnabledBuilder<FormLayout, FormLayoutBuilder>,
        HasComponentsBuilder<FormLayout, FormLayoutBuilder> {

    private FormLayoutBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static FormLayoutBuilder create() {
        return new FormLayoutBuilder();
    }

    @Override
    protected FormLayout instantiate() {
        return new FormLayout();
    }

    /**
     * {@link EntityBuilder} for {@link FormLayout.FormItem}s.
     */
    public class FormItemBuilder extends AbstractEntityBuilder<FormLayout.FormItem, FormItemBuilder> implements
            HasElementBuilder<FormLayout.FormItem, FormItemBuilder>,
            HasStyleBuilder<FormLayout.FormItem, FormItemBuilder>,
            HasComponentsBuilder<FormLayout.FormItem, FormItemBuilder>,
            ClickNotifierBuilder<FormLayout.FormItem, FormItemBuilder>,
            Configurer<FormLayout> {

        private final Function<FormLayout, FormLayout.FormItem> formItemSupplier;

        FormItemBuilder(Function<FormLayout, FormLayout.FormItem> formItemSupplier) {
            this.formItemSupplier = formItemSupplier;
        }

        @Override
        public void configure(FormLayout component) {
            apply(this.formItemSupplier.apply(component));
        }

        /**
         * Adds the currently configured item to the {@link FormLayout} being build by the returned {@link FormLayoutBuilder}.
         *
         * @return The {@link FormLayoutBuilder} that started this {@link FormLayoutBuilder.FormItemBuilder}, never null
         */
        public FormLayoutBuilder add() {
            return FormLayoutBuilder.this;
        }
    }

    /**
     * Builder method, configures the form's column behaviour.
     *
     * @see FormLayout#setResponsiveSteps(FormLayout.ResponsiveStep...)
     * @param responsiveStep
     *            The steps to exist; might <b>not</b> be null.
     * @return this
     */
    public FormLayoutBuilder setResponsiveSteps(FormLayout.ResponsiveStep... responsiveStep) {
        return configure(form -> form.setResponsiveSteps(responsiveStep));
    }

    /**
     * Builder method, configures a new item.
     *
     * @see FormLayout#addFormItem(Component, String)
     * @param msgId
     *            The label, or a message id to localize; might <b>not</b> be null.
     * @param value
     *            The value; might <b>not</b> be null.
     * @return this
     */
    public FormLayoutBuilder addFormItem(String msgId, String value) {
        return configure(new FormItemBuilder(form -> form.addFormItem(new Label(value), WebEnv.getTranslation(msgId))));
    }

    /**
     * Builder method, configures a new item.
     *
     * @see FormLayout#addFormItem(Component, String)
     * @param msgId
     *            The label, or a message id to localize; might <b>not</b> be null.
     * @param value
     *            The value; might <b>not</b> be null.
     * @return A new {@link FormItemBuilder}, never null
     */
    public FormItemBuilder configureFormItem(String msgId, String value) {
        FormItemBuilder formItemBuilder = new FormItemBuilder(form -> form.addFormItem(new Label(value), WebEnv.getTranslation(msgId)));
        configure(formItemBuilder);
        return formItemBuilder;
    }

    /**
     * Builder method, configures a new item.
     *
     * @see FormLayout#addFormItem(Component, String)
     * @param msgId
     *            The label, or a message id to localize; might <b>not</b> be null.
     * @param component
     *            The component; might <b>not</b> be null.
     * @return this
     */
    public FormLayoutBuilder addFormItem(String msgId, Component component) {
        return configure(new FormItemBuilder(form -> form.addFormItem(component, WebEnv.getTranslation(msgId))));
    }

    /**
     * Builder method, configures a new item.
     *
     * @see FormLayout#addFormItem(Component, String)
     * @param msgId
     *            The label, or a message id to localize; might <b>not</b> be null.
     * @param component
     *            The component; might <b>not</b> be null.
     * @return A new {@link FormItemBuilder}, never null
     */
    public FormItemBuilder configureFormItem(String msgId, Component component) {
        FormItemBuilder formItemBuilder = new FormItemBuilder(form -> form.addFormItem(component, WebEnv.getTranslation(msgId)));
        configure(formItemBuilder);
        return formItemBuilder;
    }

    /**
     * Builder method, configures a new item.
     *
     * @see FormLayout#addFormItem(Component, Component)
     * @param label
     *            The label; might <b>not</b> be null.
     * @param component
     *            The component; might <b>not</b> be null.
     * @return this
     */
    public FormLayoutBuilder addFormItem(Component label, Component component) {
        return configure(new FormItemBuilder(form -> form.addFormItem(component, label)));
    }

    /**
     * Builder method, configures a new item.
     *
     * @see FormLayout#addFormItem(Component, Component)
     * @param label
     *            The label; might <b>not</b> be null.
     * @param component
     *            The component; might <b>not</b> be null.
     * @return A new {@link FormItemBuilder}, never null
     */
    public FormItemBuilder configureFormItem(Component label, Component component) {
        FormItemBuilder formItemBuilder = new FormItemBuilder(form -> form.addFormItem(component, label));
        configure(formItemBuilder);
        return formItemBuilder;
    }
}
