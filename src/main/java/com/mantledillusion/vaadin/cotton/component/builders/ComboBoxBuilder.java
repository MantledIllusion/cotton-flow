package com.mantledillusion.vaadin.cotton.component.builders;

import java.util.regex.Pattern;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.GeneratedVaadinComboBox;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializablePredicate;

/**
 * {@link ComponentBuilder} for {@link ComboBox}es.
 */
public class ComboBoxBuilder<E> extends AbstractComponentBuilder<ComboBox<E>, ComboBoxBuilder<E>> implements
        HasElementBuilder<ComboBox<E>, ComboBoxBuilder<E>>,
        HasSizeBuilder<ComboBox<E>, ComboBoxBuilder<E>>,
        HasStyleBuilder<ComboBox<E>, ComboBoxBuilder<E>>,
        FocusableBuilder<ComboBox<E>, ComboBoxBuilder<E>>,
        HasEnabledBuilder<ComboBox<E>, ComboBoxBuilder<E>>,
        HasItemsBuilder<ComboBox<E>, E, ComboBoxBuilder<E>>,
        HasValueBuilder<ComboBox<E>, E, AbstractField.ComponentValueChangeEvent<ComboBox<E>, E>, ComboBoxBuilder<E>> {

    private final SerializableFunction<String, SerializablePredicate<E>> stringFilterFunction = s -> (element -> String
            .valueOf(s).equals(String.valueOf(element)));

    private ComboBoxBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static ComboBoxBuilder<Object> create() {
        return new ComboBoxBuilder<>();
    }

    /**
     * Factory method for a new instance.
     *
     * @param <E> The element type.
     * @param elementType The class type of the element; might be null.
     * @return A new instance, never null.
     */
    public static <E> ComboBoxBuilder<E> create(Class<E> elementType) {
        return new ComboBoxBuilder<>();
    }

    @Override
    protected ComboBox<E> instantiate() {
        return new ComboBox<>();
    }

    /**
     * Builder method, configures whether users should be allowed to enter own
     * values.
     *
     * @see ComboBox#setAllowCustomValue(boolean)
     * @param allowCustomValue
     *            True if custom values are allowed, false otherwise.
     * @return this
     */
    public ComboBoxBuilder<E> setAllowCustomValue(boolean allowCustomValue) {
        return configure(comboBox -> comboBox.setAllowCustomValue(allowCustomValue));
    }

    /**
     * Builder method, configures whether the {@link ComboBox} should be input
     * focussed after the page finishes loading.
     *
     * @see ComboBox#setAutofocus(boolean)
     * @param autofocus
     *            True if the {@link ComboBox} should be focussed, false otherwise.
     * @return this
     */
    public ComboBoxBuilder<E> setAutofocus(boolean autofocus) {
        return configure(comboBox -> comboBox.setAutofocus(autofocus));
    }

    /**
     * Builder method, configures the label text.
     *
     * @see ComboBox#setLabel(String)
     * @param msgId
     *            The label text or a message id to translate via {@link WebEnv};
     *            might be null.
     * @return this
     */
    public ComboBoxBuilder<E> setLabel(String msgId) {
        return configure(comboBox -> comboBox.setLabel(WebEnv.getTranslation(msgId)));
    }

    /**
     * Builder method, configures the {@link Pattern} which any custom input has to
     * match.
     *
     * @see ComboBox#setPattern(String)
     * @param pattern
     *            The pattern that input has to match; might be null.
     * @return this
     */
    public ComboBoxBuilder<E> setPattern(String pattern) {
        return configure(comboBox -> comboBox.setPattern(pattern));
    }

    /**
     * Builder method, configures the placeholder text that might be displayed when
     * nothing is selected.
     *
     * @see ComboBox#setPlaceholder(String)
     * @param msgId
     *            The placeholder text or a message id to translate via
     *            {@link WebEnv}; might be null.
     * @return this
     */
    public ComboBoxBuilder<E> setPlaceholder(String msgId) {
        return configure(comboBox -> comboBox.setPlaceholder(WebEnv.getTranslation(msgId)));
    }

    /**
     * Builder method, configures whether to prevent the user from entering invalid
     * input that might be checked against the {@link Pattern} supplied to
     * {@link #setPattern(String)}.
     *
     * @see ComboBox#setPreventInvalidInput(boolean)
     * @param preventInvalidInput
     *            True if invalid input should be prevented, false otherwise.
     * @return this
     */
    public ComboBoxBuilder<E> setPreventInvalidInput(boolean preventInvalidInput) {
        return configure(comboBox -> comboBox.setPreventInvalidInput(preventInvalidInput));
    }

    /**
     * Builder method, configures the {@link ComboBox} to be marked as required.
     *
     * @see ComboBox#setRequired(boolean)
     * @param required
     *            True if the {@link ComboBox} should be markd as required, false
     *            otherwise.
     * @return this
     */
    public ComboBoxBuilder<E> setRequired(boolean required) {
        return configure(comboBox -> comboBox.setRequired(required));
    }

    /**
     * Builder method, configures the {@link ItemLabelGenerator} that is used to
     * build a label of the currently selected item (and possibly the selectable
     * items as well if no {@link Renderer} is set to
     * {@link #setSelectedElementRenderer(Renderer)});
     * <p>
     * Renamed from the original {@link ComboBox} method to mark the difference to
     * {@link #setSelectedElementRenderer(Renderer)}.
     *
     * @see ComboBox#setItemLabelGenerator(ItemLabelGenerator)
     * @param itemLabelGenerator
     *            The {@link ItemLabelGenerator} to set; might <b>not</b> be null.
     * @return this
     */
    public ComboBoxBuilder<E> setSelectableElementRenderer(ItemLabelGenerator<E> itemLabelGenerator) {
        return configure(comboBox -> comboBox.setItemLabelGenerator(itemLabelGenerator));
    }

    /**
     * Builder method, configures the {@link Renderer} that is used to render the
     * items in the {@link ComboBox}es' drop down that are not selected (yet).
     * <p>
     * Renamed from the original {@link ComboBox} method to mark the difference to
     * {@link #setSelectableElementRenderer(ItemLabelGenerator)}.
     *
     * @see ComboBox#setRenderer(Renderer)
     * @param renderer
     *            The {@link Renderer} to set; might <b>not</b> be null.
     * @return this
     */
    public ComboBoxBuilder<E> setSelectedElementRenderer(Renderer<E> renderer) {
        return configure(comboBox -> comboBox.setRenderer(renderer));
    }

    /**
     * Builder method, configures a listener for {@link com.vaadin.flow.component.combobox.GeneratedVaadinComboBox.CustomValueSetEvent}s.
     *
     * @see ComboBox#addCustomValueSetListener(ComponentEventListener)
     * @param listener
     *          The listener to configure; might <b>not</b> be null.
     * @return this
     */
    public ComboBoxBuilder<E> addCustomValueSetListener(ComponentEventListener<GeneratedVaadinComboBox.CustomValueSetEvent<ComboBox<E>>> listener) {
        return configure(comboBox -> comboBox.addCustomValueSetListener(listener));
    }
}
