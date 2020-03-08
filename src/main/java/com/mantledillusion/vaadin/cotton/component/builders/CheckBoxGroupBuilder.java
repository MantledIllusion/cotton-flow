package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
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
		HasSizeBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>>,
		HasStyleBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>>,
		HasEnabledBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>>,
		HasItemsBuilder<CheckboxGroup<E>, E, CheckBoxGroupBuilder<E, F>>,
		HasValueBuilder<CheckboxGroup<E>, Set<E>, CheckBoxGroupBuilder<E, F>>,
		HasComponentsBuilder<CheckboxGroup<E>, CheckBoxGroupBuilder<E, F>>,
		HasDataProviderBuilder<CheckboxGroup<E>, E, F, CheckBoxGroupBuilder<E, F>> {

	private CheckBoxGroupBuilder() {}

	/**
	 * Factory method for a new instance.
	 *
	 * @return A new instance, never null.
	 */
	public static CheckBoxGroupBuilder<Object, ConfigurableFilter<Object>> create() {
		return new CheckBoxGroupBuilder();
	}

	/**
	 * Factory method for a new instance.
	 *
	 * @param <E> The element type.
	 * @param elementType The class type of the element; might be null.
	 * @return A new instance, never null.
	 */
	public static <E> CheckBoxGroupBuilder<E, ConfigurableFilter<E>> create(Class<E> elementType) {
		return new CheckBoxGroupBuilder();
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
	public static <E, F extends ConfigurableFilter<E>> CheckBoxGroupBuilder<E, F> create(Class<E> elementType,
																						 Class<F> filterType) {
		return new CheckBoxGroupBuilder();
	}

	@Override
	protected CheckboxGroup<E> instantiate() {
		return new CheckboxGroup<>();
	}

	/**
	 * Builder method, configures the label text.
	 * 
	 * @see CheckboxGroup#setLabel(String)
	 * @param msgId
	 *            The label text or a message id to translate via {@link WebEnv}; might be null.
	 * @return this
	 */
	public CheckBoxGroupBuilder setLabel(String msgId) {
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
	public CheckBoxGroupBuilder setItemEnabledProvider(SerializablePredicate<E> itemEnabledProvider) {
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
	public CheckBoxGroupBuilder setItemLabelGenerator(ItemLabelGenerator<E> itemLabelGenerator) {
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
	public CheckBoxGroupBuilder setItemLabelMessageIdPrefix(String messageIdPrefix) {
		return configure(checkBoxGroup -> checkBoxGroup.setItemLabelGenerator(
				item -> WebEnv.getTranslation(StringUtils.defaultIfBlank(messageIdPrefix, StringUtils.EMPTY)+item)));
	}
}
