package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.function.SerializablePredicate;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * {@link ComponentBuilder} for {@link CheckboxGroup}s.
 *
 * @param <E> The element type of the {@link CheckboxGroup}
 * @param <F> The filter type of the {@link CheckboxGroup}
 */
public class CheckBoxGroupBuilder<E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> extends
		AbstractComponentBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>> implements
		HasElementBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>>,
		HasSizeBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>>,
		HasThemeVariantBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>, CheckboxGroupVariant>,
		HasStyleBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>>,
		HasEnabledBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>>,
		HasItemsBuilder<CheckboxGroup<E>, E, CheckBoxGroupBuilder<E, F>>,
		HasValueBuilder<CheckboxGroup<E>, Set<E>, AbstractField.ComponentValueChangeEvent<CheckboxGroup<E>, Set<E>>, CheckBoxGroupBuilder<E, F>>,
		HasComponentsBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>>,
        HasSimpleDataProviderBuilder<CheckboxGroup<E>, E, F, CheckBoxGroupBuilder<E, F>> {

	private CheckBoxGroupBuilder() {}

	/**
	 * Factory method for a new instance.
	 *
	 * @return A new instance, never null.
	 */
	public static CheckBoxGroupBuilder<Object, HasDataProviderBuilder.ConfigurableFilter<Object>> create() {
		return new CheckBoxGroupBuilder<>();
	}

	/**
	 * Factory method for a new instance.
	 *
	 * @param <E> The element type.
	 * @param elementType The class type of the element; might be null.
	 * @return A new instance, never null.
	 */
	public static <E> CheckBoxGroupBuilder<E, HasDataProviderBuilder.ConfigurableFilter<E>> create(Class<E> elementType) {
		return new CheckBoxGroupBuilder<>();
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
	public static <E, F extends HasDataProviderBuilder.ConfigurableFilter<E>> CheckBoxGroupBuilder<E, F> create(Class<E> elementType,
																												Class<F> filterType) {
		return new CheckBoxGroupBuilder<>();
	}

	@Override
	protected CheckboxGroup<E> instantiate() {
		return new CheckboxGroup<>();
	}

	@Override
	public String toVariantName(CheckboxGroupVariant variant) {
		return variant.getVariantName();
	}

	/**
	 * Builder method, configures the label text.
	 * 
	 * @see CheckboxGroup#setLabel(String)
	 * @param msgId
	 *            The label text or a message id to translate via {@link WebEnv}; might be null.
	 * @return this
	 */
	public CheckBoxGroupBuilder<E, F> setLabel(String msgId) {
		return configure(checkBoxGroup -> checkBoxGroup.setLabel(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Builder method, configures the predicate to use for item enablement.
	 *
	 * @see CheckboxGroup#setItemEnabledProvider(SerializablePredicate)
	 * @param itemEnabledProvider
	 *            The predicate enabling items; might <b>not</b> be null.
	 * @return this
	 */
	public CheckBoxGroupBuilder<E, F> setItemEnabledProvider(SerializablePredicate<E> itemEnabledProvider) {
		return configure(checkBoxGroup -> checkBoxGroup.setItemEnabledProvider(itemEnabledProvider));
	}

	/**
	 * Builder method, configures the generator to use for item label generation.
	 *
	 * @see CheckboxGroup#setItemLabelGenerator(ItemLabelGenerator)
	 * @param itemLabelGenerator
	 *            The generator for item labels or message ids to translate via {@link WebEnv}; might <b>not</b> be null.
	 * @return this
	 */
	public CheckBoxGroupBuilder<E, F> setItemLabelGenerator(ItemLabelGenerator<E> itemLabelGenerator) {
		return configure(checkBoxGroup -> checkBoxGroup.setItemLabelGenerator(
				item -> WebEnv.getTranslation(itemLabelGenerator.apply(item))));
	}

	/**
	 * Builder method, configures a {@link WebEnv} based generator for item labels using a prefix and
	 * {@link Object#toString()} for building a message id.
	 *
	 * @see CheckboxGroup#setItemLabelGenerator(ItemLabelGenerator)
	 * @param messageIdPrefix
	 *            The message id prefix to append an item's {@link Object#toString()} value to; might be null.
	 * @return this
	 */
	public CheckBoxGroupBuilder<E, F> setItemLabelMessageIdPrefix(String messageIdPrefix) {
		return configure(checkBoxGroup -> checkBoxGroup.setItemLabelGenerator(
				item -> WebEnv.getTranslation(StringUtils.defaultIfBlank(messageIdPrefix, StringUtils.EMPTY)+item)));
	}

	/**
	 * Builder method, configures a listener for {@link com.vaadin.flow.data.selection.MultiSelectionEvent}s.
	 * 
	 * @see CheckboxGroup#addSelectionListener(MultiSelectionListener)
	 * @param listener 
	 * 			The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	public CheckBoxGroupBuilder<E, F> addSelectionListener(MultiSelectionListener<CheckboxGroup<E>, E> listener) {
		return configure(checkBoxGroup -> checkBoxGroup.addSelectionListener(listener));
	}
}
