package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.SerializablePredicate;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link ComponentBuilder} for {@link RadioButtonGroup}s.
 */
public class RadioButtonGroupBuilder<E> extends AbstractComponentBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E>>
		implements HasStyleBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E>>,
		HasEnabledBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E>>,
		HasItemsBuilder<RadioButtonGroup<E>, E, RadioButtonGroupBuilder<E>>,
		HasValueBuilder<RadioButtonGroup<E>, E, RadioButtonGroupBuilder<E>>,
		HasComponentsBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E>>,
		HasDataProviderBuilder<RadioButtonGroup<E>, E, RadioButtonGroupBuilder<E>> {

	@Override
	public RadioButtonGroup<E> instantiate() {
		return new RadioButtonGroup<>();
	}

	/**
	 * Builder method, configures the label text.
	 * 
	 * @see RadioButtonGroup#setLabel(String)
	 * @param msgId
	 *            The label text or a message id to translate via {@link WebEnv}; might be null.
	 * @return this
	 */
	public RadioButtonGroupBuilder setLabel(String msgId) {
		return configure(radioButtonGroup -> radioButtonGroup.setLabel(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Builder method, configures the predicate to use for item enablement.
	 *
	 * @see RadioButtonGroup#setItemEnabledProvider(SerializablePredicate)
	 * @param itemEnabledProvider
	 *            The predicate enabling items; might <b>not</b> be null.
	 * @return this
	 */
	public RadioButtonGroupBuilder setItemEnabledProvider(SerializablePredicate<E> itemEnabledProvider) {
		return configure(radioButtonGroup -> radioButtonGroup.setItemEnabledProvider(itemEnabledProvider));
	}

	/**
	 * Builder method, configures the renderer to use for item component generation.
	 *
	 * @see RadioButtonGroup#setRenderer(ComponentRenderer)
	 * @param renderer
	 *            The generator for item labels or message ids to translate via {@link WebEnv}; might <b>not</b> be null.
	 * @return this
	 */
	public RadioButtonGroupBuilder setRenderer(ComponentRenderer<? extends Component, E> renderer) {
		return configure(radioButtonGroup -> radioButtonGroup.setRenderer(renderer));
	}

	/**
	 * Builder method, configures a {@link WebEnv} based {@link TextRenderer} for item labels using a prefix and
	 * {@link Object#toString()} for building a message id.
	 *
	 * @see RadioButtonGroup#setRenderer(ComponentRenderer)
	 * @param messageIdPrefix
	 *            The message id prefix to append an item's {@link Object#toString()} value to; might be null.
	 * @return this
	 */
	public RadioButtonGroupBuilder setRenderMessageIdPrefix(String messageIdPrefix) {
		return configure(radioButtonGroup -> radioButtonGroup.setRenderer(new TextRenderer<>(
				item -> WebEnv.getTranslation(StringUtils.defaultIfBlank(messageIdPrefix, StringUtils.EMPTY)+item))));
	}
}
