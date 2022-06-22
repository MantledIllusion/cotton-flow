package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.IntegerField;

/**
 * {@link ComponentBuilder} for {@link IntegerField}s.
 */
public class IntegerFieldBuilder extends AbstractComponentBuilder<IntegerField, IntegerFieldBuilder> implements
        HasElementBuilder<IntegerField, IntegerFieldBuilder>,
        HasThemeBuilder<IntegerField, IntegerFieldBuilder>,
        FocusableBuilder<IntegerField, IntegerFieldBuilder>,
        HasStyleBuilder<IntegerField, IntegerFieldBuilder>,
        HasSizeBuilder<IntegerField, IntegerFieldBuilder>,
        HasValueChangeModeBuilder<IntegerField, IntegerFieldBuilder>,
        HasAutocompleteBuilder<IntegerField, IntegerFieldBuilder>,
        HasAutocapitalizeBuilder<IntegerField, IntegerFieldBuilder>,
        HasAutocorrectBuilder<IntegerField, IntegerFieldBuilder>,
        HasEnabledBuilder<IntegerField, IntegerFieldBuilder>,
        HasValueBuilder<IntegerField, Integer, AbstractField.ComponentValueChangeEvent<IntegerField, Integer>, IntegerFieldBuilder>,
        CompositionNotifierBuilder<IntegerField, IntegerFieldBuilder>,
        InputNotifierBuilder<IntegerField, IntegerFieldBuilder>,
        KeyNotifierBuilder<IntegerField, IntegerFieldBuilder> {

    private IntegerFieldBuilder() {}
    
    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static IntegerFieldBuilder create() {
        return new IntegerFieldBuilder();
    }

    @Override
    protected IntegerField instantiate() {
        return new IntegerField();
    }

    /**
     * Builder method, configures whether the {@link IntegerField} has to be focused
     * automatically when the field's page finished loading.
     *
     * @param autofocus True if the {@link IntegerField} has to receive the auto focus, false
     *                  otherwise.
     * @return this
     * @see IntegerField#setAutofocus(boolean)
     */
    public IntegerFieldBuilder setAutofocus(boolean autofocus) {
        return configure(IntegerField -> IntegerField.setAutofocus(autofocus));
    }

    /**
     * Builder method, configures the label to set.
     *
     * @param msgId The text to set to the label, or a message id to localize; might
     *              be null.
     * @return this
     * @see IntegerField#setLabel(String)
     */
    public IntegerFieldBuilder setLabel(String msgId) {
        return configure(IntegerField -> IntegerField.setLabel(WebEnv.getTranslation(msgId)));
    }

    /**
     * Builder method, configures the maximum the {@link IntegerField}s value might grow up to.
     *
     * @param max The max value.
     * @return this
     */
    public IntegerFieldBuilder setMax(int max) {
        return configure(IntegerField -> IntegerField.setMax(max));
    }

    /**
     * Builder method, configures the minimum the {@link IntegerField}s value might shrink down to.
     *
     * @param min The min value.
     * @return this
     */
    public IntegerFieldBuilder setMin(int min) {
        return configure(IntegerField -> IntegerField.setMin(min));
    }

    /**
     * Builder method, configures the placeholder text to show as long as the
     * {@link IntegerField} is empty.
     *
     * @param msgId The placeholder text or a message id to translate via
     *              {@link WebEnv}; might be null.
     * @return this
     * @see IntegerField#setPlaceholder(String)
     */
    public IntegerFieldBuilder setPlaceholder(String msgId) {
        return configure(IntegerField -> IntegerField.setPlaceholder(WebEnv.getTranslation(msgId)));
    }
}
