package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;

/**
 * {@link ComponentBuilder} for {@link TextArea}s.
 */
public class TextAreaBuilder extends AbstractComponentBuilder<TextArea, TextAreaBuilder> implements
		HasSizeBuilder<TextArea, TextAreaBuilder>,
		HasThemeVariantBuilder<TextArea, TextAreaBuilder, TextAreaVariant>,
		HasStyleBuilder<TextArea, TextAreaBuilder>,
		HasValueChangeModeBuilder<TextArea, TextAreaBuilder>,
		HasAutocompleteBuilder<TextArea, TextAreaBuilder>,
		HasAutocapitalizeBuilder<TextArea, TextAreaBuilder>,
		HasAutocorrectBuilder<TextArea, TextAreaBuilder>,
		FocusableBuilder<TextArea, TextAreaBuilder>,
		HasEnabledBuilder<TextArea, TextAreaBuilder>,
		HasValueBuilder<TextArea, String, TextAreaBuilder> {

	private TextAreaBuilder() {}

	/**
	 * Factory method for a new instance.
	 *
	 * @return A new instance, never null.
	 */
	public static TextAreaBuilder create() {
		return new TextAreaBuilder();
	}

	@Override
	protected TextArea instantiate() {
		return new TextArea();
	}

	@Override
	public String toVariantName(TextAreaVariant variant) {
		return variant.getVariantName();
	}

	/**
	 * Builder method, configures whether the {@link TextArea} has to be focused automatically when the field's page
	 * finished loading.
	 * 
	 * @see TextArea#setAutofocus(boolean)
	 * @param autofocus
	 *            True if the {@link TextArea} has to receive the auto focus, false otherwise.
	 * @return this
	 */
	public TextAreaBuilder setAutofocus(boolean autofocus) {
		return configure(textArea -> textArea.setAutofocus(autofocus));
	}

	/**
	 * Builder method, configures the label to set.
	 * 
	 * @see TextArea#setLabel(String)
	 * @param msgId
	 *            The text to set to the label, or a message id to localize; might be null.
	 * @return this
	 */
	public TextAreaBuilder setLabel(String msgId) {
		return configure(textArea -> textArea.setLabel(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Builder method, configures the maximum length the {@link TextArea}s value might grow up to.
	 * 
	 * @see TextArea#setMaxLength(int)
	 * @param maxLength
	 *            The max length.
	 * @return this
	 */
	public TextAreaBuilder setMaxLength(int maxLength) {
		return configure(textArea -> textArea.setMaxLength(maxLength));
	}

	/**
	 * Builder method, configures the minimum length the {@link TextArea}s value might shrink down to.
	 * 
	 * @see TextArea#setMinLength(int)
	 * @param minLength
	 *            The min length.
	 * @return this
	 */
	public TextAreaBuilder setMinLength(int minLength) {
		return configure(textArea -> textArea.setMinLength(minLength));
	}

	/**
	 * Builder method, configures the placeholder text to show as long as the {@link TextArea} is empty.
	 * 
	 * @see TextArea#setPlaceholder(String)
	 * @param msgId
	 *            The placeholder text or a message id to translate via {@link WebEnv}; might be null.
	 * @return this
	 */
	public TextAreaBuilder setPlaceholder(String msgId) {
		return configure(textArea -> textArea.setPlaceholder(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Builder method, configures whether the {@link TextArea} might actively prevent an input that does not match.
	 * 
	 * @see TextArea#setPreventInvalidInput(boolean)
	 * @param preventInvalidInput
	 *            True if the input of non-matching values should be prevented, false otherwise.
	 * @return this
	 */
	public TextAreaBuilder setPreventInvalidInput(boolean preventInvalidInput) {
		return configure(textArea -> textArea.setPreventInvalidInput(preventInvalidInput));
	}

	/**
	 * Builder method, configures the selection to be required.
	 * 
	 * @see TextArea#setRequired(boolean)
	 * @param required
	 *            True if the week number should be marked required, false otherwise.
	 * @return this
	 */
	public TextAreaBuilder setRequired(boolean required) {
		return configure(textArea -> textArea.setRequired(required));
	}
}