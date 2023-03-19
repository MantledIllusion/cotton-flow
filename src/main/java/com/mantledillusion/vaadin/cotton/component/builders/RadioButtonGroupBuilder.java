package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.SerializablePredicate;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link ComponentBuilder} for {@link RadioButtonGroup}s.
 *
 * @param <E> The element type of the {@link RadioButtonGroup}
 * @param <F> The filter type of the {@link RadioButtonGroup}
 */
public class RadioButtonGroupBuilder<E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> extends
		AbstractComponentBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E, F>> implements
		HasElementBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E, F>>,
		HasThemeVariantBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E, F>, RadioGroupVariant>,
		HasStyleBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E, F>>,
		HasEnabledBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E, F>>,
		HasItemsBuilder<RadioButtonGroup<E>, E, RadioButtonGroupBuilder<E, F>>,
		HasValueBuilder<RadioButtonGroup<E>, E, AbstractField.ComponentValueChangeEvent<RadioButtonGroup<E>, E>, RadioButtonGroupBuilder<E, F>>,
		HasComponentsBuilder<RadioButtonGroup<E>, RadioButtonGroupBuilder<E, F>>,
        HasSimpleDataProviderBuilder<RadioButtonGroup<E>, E, F, RadioButtonGroupBuilder<E, F>> {

	private RadioButtonGroupBuilder() {}

	/**
	 * Factory method for a new instance.
	 *
	 * @return A new instance, never null.
	 */
	public static RadioButtonGroupBuilder<Object, HasDataProviderBuilder.ConfigurableFilter<Object>> create() {
		return new RadioButtonGroupBuilder<>();
	}

	/**
	 * Factory method for a new instance.
	 *
	 * @param <E> The element type.
	 * @param elementType The class type of the element; might be null.
	 * @return A new instance, never null.
	 */
	public static <E> RadioButtonGroupBuilder<E, HasDataProviderBuilder.ConfigurableFilter<E>> create(Class<E> elementType) {
		return new RadioButtonGroupBuilder<>();
	}

	/**
	 * Factory method for a new instance.
	 *
	 * @param <E> The element type.
	 * @param <F> The filter type.
	 * @param elementType The class type of the element; might be null.
	 * @param filterType The class type of the filter; might be null.
	 * @return A new instance, never null.
	 */
	public static <E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> RadioButtonGroupBuilder<E, F> create(Class<E> elementType,
																												   Class<F> filterType) {
		return new RadioButtonGroupBuilder<>();
	}

	@Override
	protected RadioButtonGroup<E> instantiate() {
		return new RadioButtonGroup<>();
	}

	@Override
	public String toVariantName(RadioGroupVariant variant) {
		return variant.getVariantName();
	}

	/**
	 * Builder method, configures the label text.
	 * 
	 * @see RadioButtonGroup#setLabel(String)
	 * @param msgId
	 *            The label text or a message id to translate via {@link WebEnv}; might be null.
	 * @return this
	 */
	public RadioButtonGroupBuilder<E, F> setLabel(String msgId) {
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
	public RadioButtonGroupBuilder<E, F> setItemEnabledProvider(SerializablePredicate<E> itemEnabledProvider) {
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
	public RadioButtonGroupBuilder<E, F> setRenderer(ComponentRenderer<? extends Component, E> renderer) {
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
	public RadioButtonGroupBuilder<E, F> setRenderMessageIdPrefix(String messageIdPrefix) {
		return configure(radioButtonGroup -> radioButtonGroup.setRenderer(new TextRenderer<>(
				item -> WebEnv.getTranslation(StringUtils.defaultIfBlank(messageIdPrefix, StringUtils.EMPTY)+item))));
	}
}
