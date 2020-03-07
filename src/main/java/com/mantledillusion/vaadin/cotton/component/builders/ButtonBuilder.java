package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;

/**
 * {@link ComponentBuilder} for {@link Button}s.
 */
public class ButtonBuilder extends AbstractComponentBuilder<Button, ButtonBuilder>
		implements HasSizeBuilder<Button, ButtonBuilder>, HasStyleBuilder<Button, ButtonBuilder>,
		HasTextBuilder<Button, ButtonBuilder>, HasEnabledBuilder<Button, ButtonBuilder>,
		FocusableBuilder<Button, ButtonBuilder>, ClickableBuilder<Button, ButtonBuilder> {

	@Override
	protected Button instantiate() {
		return new Button();
	}

	/**
	 * Builder method, configures whether the {@link Button} has to be focused
	 * automatically when the field's page finished loading.
	 * 
	 * @see Button#setAutofocus(boolean)
	 * @param autofocus
	 *            True if the {@link Button} has to receive the auto focus, false
	 *            otherwise.
	 * @return this
	 */
	public ButtonBuilder setAutofocus(boolean autofocus) {
		return configure(button -> button.setAutofocus(autofocus));
	}

	/**
	 * Builder method, configures the {@link Component} that should be displayed as
	 * icon.
	 * 
	 * @see Button#setIcon(Component)
	 * @param icon
	 *            The {@link Component} to display as icon; might be null.
	 * @return this
	 */
	public ButtonBuilder setIconAfterText(Component icon) {
		return configure(button -> button.setIcon(icon));
	}

	/**
	 * Builder method, configures whether the {@link Button}'s icon should be
	 * displayed after its text.
	 * 
	 * @see Button#setIconAfterText(boolean)
	 * @param iconAfterText
	 *            True if the should be displayed after the text, false otherwise.
	 * @return this
	 */
	public ButtonBuilder setIconAfterText(boolean iconAfterText) {
		return configure(button -> button.setIconAfterText(iconAfterText));
	}

	/**
	 * Builder method, configures the {@link Button}'s text.
	 * 
	 * @see Button#setText(String)
	 * @param msgId
	 *            The text or a message id to translate via {@link WebEnv}; might be
	 *            null.
	 * @return this
	 */
	public ButtonBuilder setText(String msgId) {
		return configure(button -> button.setText(WebEnv.getTranslation(msgId)));
	}
}
