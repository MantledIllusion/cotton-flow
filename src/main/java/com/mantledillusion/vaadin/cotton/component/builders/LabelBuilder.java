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
public class LabelBuilder extends AbstractComponentBuilder<Label, LabelBuilder> implements
		HasSizeBuilder<Label, LabelBuilder>,
		HasStyleBuilder<Label, LabelBuilder>,
		HasEnabledBuilder<Label, LabelBuilder>,
		HasTextBuilder<Label, LabelBuilder> {

	private LabelBuilder() {}

	/**
	 * Factory method for a new instance.
	 *
	 * @return A new instance, never null.
	 */
	public static LabelBuilder create() {
		return new LabelBuilder();
	}

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

	/**
	 * Builder method, configures whether the {@link Label}'s text can be wrapped on white spaces or not.
	 *
	 * @see com.vaadin.flow.dom.Style#set(String, String)
	 * @param wrap
	 *            True if the text can be wrapped, false otherwise.
	 * @return this
	 */
	public LabelBuilder setWrap(boolean wrap) {
		return configure(label -> label.getElement().getStyle().set("white-space", wrap ? "pre-wrap" : "nowrap"));
	}
}