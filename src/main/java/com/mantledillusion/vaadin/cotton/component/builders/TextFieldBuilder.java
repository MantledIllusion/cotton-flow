package com.mantledillusion.vaadin.cotton.component.builders;

import java.util.regex.Pattern;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

/**
 * {@link ComponentBuilder} for {@link TextField}s.
 */
public class TextFieldBuilder extends AbstractComponentBuilder<TextField, TextFieldBuilder> implements
		HasElementBuilder<TextField, TextFieldBuilder>,
		HasSizeBuilder<TextField, TextFieldBuilder>,
		HasThemeVariantBuilder<TextField, TextFieldBuilder, TextFieldVariant>,
		HasStyleBuilder<TextField, TextFieldBuilder>,
		HasValueChangeModeBuilder<TextField, TextFieldBuilder>,
		HasAutocompleteBuilder<TextField, TextFieldBuilder>,
		HasAutocapitalizeBuilder<TextField, TextFieldBuilder>,
		HasAutocorrectBuilder<TextField, TextFieldBuilder>,
		FocusableBuilder<TextField, TextFieldBuilder>,
		HasEnabledBuilder<TextField, TextFieldBuilder>,
		HasValueBuilder<TextField, String, AbstractField.ComponentValueChangeEvent<TextField, String>, TextFieldBuilder>,
		CompositionNotifierBuilder<TextField, TextFieldBuilder>,
		InputNotifierBuilder<TextField, TextFieldBuilder>,
		KeyNotifierBuilder<TextField, TextFieldBuilder> {

	private TextFieldBuilder() {}

	/**
	 * Factory method for a new instance.
	 *
	 * @return A new instance, never null.
	 */
	public static TextFieldBuilder create() {
		return new TextFieldBuilder();
	}

	@Override
	protected TextField instantiate() {
		return new TextField();
	}

	@Override
	public String toVariantName(TextFieldVariant variant) {
		return variant.getVariantName();
	}

	/**
	 * Builder method, configures whether the {@link TextField} has to be focused
	 * automatically when the field's page finished loading.
	 * 
	 * @see TextField#setAutofocus(boolean)
	 * @param autofocus
	 *            True if the {@link TextField} has to receive the auto focus, false
	 *            otherwise.
	 * @return this
	 */
	public TextFieldBuilder setAutofocus(boolean autofocus) {
		return configure(textField -> textField.setAutofocus(autofocus));
	}

	/**
	 * Builder method, configures the label to set.
	 * 
	 * @see TextField#setLabel(String)
	 * @param msgId
	 *            The text to set to the label, or a message id to localize; might
	 *            be null.
	 * @return this
	 */
	public TextFieldBuilder setLabel(String msgId) {
		return configure(textField -> textField.setLabel(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Builder method, configures the maximum length the {@link TextField}s value
	 * might grow up to.
	 * 
	 * @see TextField#setMaxLength(int)
	 * @param maxLength
	 *            The max length.
	 * @return this
	 */
	public TextFieldBuilder setMaxLength(int maxLength) {
		return configure(textField -> textField.setMaxLength(maxLength));
	}

	/**
	 * Builder method, configures the minimum length the {@link TextField}s value
	 * might shrink down to.
	 * 
	 * @see TextField#setMinLength(int)
	 * @param minLength
	 *            The min length.
	 * @return this
	 */
	public TextFieldBuilder setMinLength(int minLength) {
		return configure(textField -> textField.setMinLength(minLength));
	}

	/**
	 * Builder method, configures {@link Pattern} the {@link TextField}'s content
	 * has to match.
	 * 
	 * @see TextField#setPattern(String)
	 * @param pattern
	 *            The pattern the value has to match; might be null.
	 * @return this
	 */
	public TextFieldBuilder setPattern(String pattern) {
		return configure(textField -> textField.setPattern(pattern));
	}

	/**
	 * Builder method, configures the placeholder text to show as long as the
	 * {@link TextField} is empty.
	 * 
	 * @see TextField#setPlaceholder(String)
	 * @param msgId
	 *            The placeholder text or a message id to translate via
	 *            {@link WebEnv}; might be null.
	 * @return this
	 */
	public TextFieldBuilder setPlaceholder(String msgId) {
		return configure(textField -> textField.setPlaceholder(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Builder method, configures whether the {@link TextField} might actively
	 * prevent an input that does not match the pattern given to
	 * {@link #setPattern(String)}.
	 * 
	 * @see TextField#setPreventInvalidInput(boolean)
	 * @param preventInvalidInput
	 *            True if the input of non-matching values should be prevented,
	 *            false otherwise.
	 * @return this
	 */
	public TextFieldBuilder setPreventInvalidInput(boolean preventInvalidInput) {
		return configure(textField -> textField.setPreventInvalidInput(preventInvalidInput));
	}

	/**
	 * Builder method, configures the selection to be required.
	 * 
	 * @see TextField#setRequired(boolean)
	 * @param required
	 *            True if the week number should be marked required, false
	 *            otherwise.
	 * @return this
	 */
	public TextFieldBuilder setRequired(boolean required) {
		return configure(textField -> textField.setRequired(required));
	}
}