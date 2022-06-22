package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.NumberField;

/**
 * {@link ComponentBuilder} for {@link NumberField}s.
 */
public class NumberFieldBuilder extends AbstractComponentBuilder<NumberField, NumberFieldBuilder> implements
        HasElementBuilder<NumberField, NumberFieldBuilder>,
        HasThemeBuilder<NumberField, NumberFieldBuilder>,
        FocusableBuilder<NumberField, NumberFieldBuilder>,
        HasStyleBuilder<NumberField, NumberFieldBuilder>,
        HasSizeBuilder<NumberField, NumberFieldBuilder>,
        HasValueChangeModeBuilder<NumberField, NumberFieldBuilder>,
        HasAutocompleteBuilder<NumberField, NumberFieldBuilder>,
        HasAutocapitalizeBuilder<NumberField, NumberFieldBuilder>,
        HasAutocorrectBuilder<NumberField, NumberFieldBuilder>,
        HasEnabledBuilder<NumberField, NumberFieldBuilder>,
        HasValueBuilder<NumberField, Double, AbstractField.ComponentValueChangeEvent<NumberField, Double>, NumberFieldBuilder>,
        CompositionNotifierBuilder<NumberField, NumberFieldBuilder>,
        InputNotifierBuilder<NumberField, NumberFieldBuilder>,
        KeyNotifierBuilder<NumberField, NumberFieldBuilder> {

    private NumberFieldBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static NumberFieldBuilder create() {
        return new NumberFieldBuilder();
    }

    @Override
    protected NumberField instantiate() {
        return new NumberField();
    }

    /**
     * Builder method, configures whether the {@link NumberField} has to be focused
     * automatically when the field's page finished loading.
     *
     * @param autofocus True if the {@link NumberField} has to receive the auto focus, false
     *                  otherwise.
     * @return this
     * @see NumberField#setAutofocus(boolean)
     */
    public NumberFieldBuilder setAutofocus(boolean autofocus) {
        return configure(numberField -> numberField.setAutofocus(autofocus));
    }

    /**
     * Builder method, configures the label to set.
     *
     * @param msgId The text to set to the label, or a message id to localize; might
     *              be null.
     * @return this
     * @see NumberField#setLabel(String)
     */
    public NumberFieldBuilder setLabel(String msgId) {
        return configure(numberField -> numberField.setLabel(WebEnv.getTranslation(msgId)));
    }

    /**
     * Builder method, configures the maximum the {@link NumberField}s value might grow up to.
     *
     * @param max The max value.
     * @return this
     */
    public NumberFieldBuilder setMax(double max) {
        return configure(numberField -> numberField.setMax(max));
    }

    /**
     * Builder method, configures the minimum the {@link NumberField}s value might shrink down to.
     *
     * @param min The min value.
     * @return this
     */
    public NumberFieldBuilder setMin(double min) {
        return configure(numberField -> numberField.setMin(min));
    }

    /**
     * Builder method, configures the placeholder text to show as long as the
     * {@link NumberField} is empty.
     *
     * @param msgId The placeholder text or a message id to translate via
     *              {@link WebEnv}; might be null.
     * @return this
     * @see NumberField#setPlaceholder(String)
     */
    public NumberFieldBuilder setPlaceholder(String msgId) {
        return configure(numberField -> numberField.setPlaceholder(WebEnv.getTranslation(msgId)));
    }
}
