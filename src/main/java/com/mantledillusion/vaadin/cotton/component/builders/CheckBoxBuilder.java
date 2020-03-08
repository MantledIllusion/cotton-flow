package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.checkbox.Checkbox;

/**
 * {@link ComponentBuilder} for {@link Checkbox}es.
 */
public class CheckBoxBuilder extends AbstractComponentBuilder<Checkbox, CheckBoxBuilder>
		implements HasSizeBuilder<Checkbox, CheckBoxBuilder>, HasStyleBuilder<Checkbox, CheckBoxBuilder>,
		FocusableBuilder<Checkbox, CheckBoxBuilder>, HasEnabledBuilder<Checkbox, CheckBoxBuilder>,
		HasValueBuilder<Checkbox, Boolean, CheckBoxBuilder>, ClickableBuilder<Checkbox, CheckBoxBuilder> {

	private CheckBoxBuilder() {}

	/**
	 * Factory method for a new instance.
	 *
	 * @return A new instance, never null.
	 */
	public static CheckBoxBuilder create() {
		return new CheckBoxBuilder();
	}

	@Override
	protected Checkbox instantiate() {
		return new Checkbox();
	}

	/**
	 * Builder method, configures the accessibility label.
	 * 
	 * @see Checkbox#setAriaLabel(String)
	 * @param ariaLabel
	 *            The accessibility label; might be null.
	 * @return this
	 */
	public CheckBoxBuilder setAriaLabel(String ariaLabel) {
		return configure(checkBox -> checkBox.setAriaLabel(ariaLabel));
	}

	/**
	 * Builder method, configures whether the {@link Checkbox} should be input
	 * focussed after the page finishes loading.
	 * 
	 * @see Checkbox#setAutofocus(boolean)
	 * @param autofocus
	 *            True if the {@link Checkbox} should be focussed, false otherwise.
	 * @return this
	 */
	public CheckBoxBuilder setAutofocus(boolean autofocus) {
		return configure(checkBox -> checkBox.setAutofocus(autofocus));
	}

	/**
	 * Builder method, configures the indeterminate state.
	 * 
	 * @see Checkbox#setIndeterminate(boolean)
	 * @param indeterminate
	 *            True if the {@link Checkbox} should be indeterminate, false
	 *            otherwise.
	 * @return this
	 */
	public CheckBoxBuilder setIndeterminate(boolean indeterminate) {
		return configure(checkBox -> checkBox.setIndeterminate(indeterminate));
	}

	/**
	 * Builder method, configures the label text.
	 * 
	 * @see Checkbox#setLabel(String)
	 * @param msgId
	 *            The label text or a message id to translate via {@link WebEnv};
	 *            might be null.
	 * @return this
	 */
	public CheckBoxBuilder setLabel(String msgId) {
		return configure(checkBox -> checkBox.setLabel(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Builder method, configures the label text rendered as HTML.
	 * 
	 * @see Checkbox#setLabel(String)
	 * @param msgId
	 *            The label html or a message id to translate via {@link WebEnv};
	 *            might be null.
	 * @return this
	 */
	public CheckBoxBuilder setLabelAsHtml(String msgId) {
		return configure(checkBox -> checkBox.setLabelAsHtml(WebEnv.getTranslation(msgId)));
	}

}
