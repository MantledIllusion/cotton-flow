package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasEnabledBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasSizeBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasStyleBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasTextBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;

/**
 * {@link ComponentBuilder} for {@link Label}s.
 */
public class LabelBuilder extends AbstractComponentBuilder<Label, LabelBuilder>
		implements HasSizeBuilder<Label, LabelBuilder>, HasStyleBuilder<Label, LabelBuilder>,
		HasEnabledBuilder<Label, LabelBuilder>, HasTextBuilder<Label, LabelBuilder> {

	@Override
	protected Label instantiate() {
		return new Label();
	}

	/**
	 * Builder method, configures the component this {@link Label} is for.
	 * 
	 * @see Label#setFor(String)
	 * @param id
	 *            The id of the {@link Component} who this {@link Label} is for;
	 *            might be null.
	 * @return this
	 */
	public LabelBuilder setFor(String id) {
		return configure(label -> label.setFor(id));
	}

	/**
	 * Builder method, configures the component this {@link Label} is for.
	 * 
	 * @see Label#setFor(Component)
	 * @param c
	 *            The {@link Component} who this {@link Label} is for; might
	 *            <b>not</b> be null.
	 * @return this
	 */
	public LabelBuilder setFor(Component c) {
		return configure(label -> label.setFor(c));
	}
}