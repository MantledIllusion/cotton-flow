package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.Converter;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;

/**
 * {@link ComponentBuilder} for {@link ProgressBar}s.
 */
public class ProgressBarBuilder extends AbstractComponentBuilder<ProgressBar, ProgressBarBuilder> implements
        HasElementBuilder<ProgressBar, ProgressBarBuilder>,
        HasSizeBuilder<ProgressBar, ProgressBarBuilder>,
        HasThemeVariantBuilder<ProgressBar, ProgressBarBuilder, ProgressBarVariant>,
        HasStyleBuilder<ProgressBar, ProgressBarBuilder> {

    private ProgressBarBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static ProgressBarBuilder create() {
        return new ProgressBarBuilder();
    }

    @Override
    protected ProgressBar instantiate() {
        return new ProgressBar();
    }

    @Override
    public String toVariantName(ProgressBarVariant variant) {
        return variant.getVariantName();
    }

    /**
     * Builder method, configures the {@link ProgressBar} range's minimum value.
     *
     * @see ProgressBar#setMin(double)
     * @param min
     *            The min value of the {@link ProgressBar}'s range.
     * @return this
     */
    public ProgressBarBuilder setMin(double min) {
        return configure(progressBar -> progressBar.setMin(min));
    }

    /**
     * Builder method, configures the {@link ProgressBar} range's maximum value.
     *
     * @see ProgressBar#setMax(double)
     * @param max
     *            The max value of the {@link ProgressBar}'s range.
     * @return this
     */
    public ProgressBarBuilder setMax(double max) {
        return configure(progressBar -> progressBar.setMax(max));
    }

    /**
     * Builder method, configures the {@link ProgressBar}'s current value.
     *
     * @see ProgressBar#setValue(double)
     * @param value
     *            The value of the {@link ProgressBar}.
     * @return this
     */
    public ProgressBarBuilder setValue(double value) {
        return configure(progressBar -> progressBar.setValue(value));
    }

    /**
     * Builder method, configures the {@link ProgressBar} to be indeterminate.
     *
     * @see ProgressBar#setIndeterminate(boolean)
     * @param indeterminate
     *            True if the {@link ProgressBar} should be indeterminate, false otherwise.
     * @return this
     */
    public ProgressBarBuilder setIndeterminate(boolean indeterminate) {
        return configure(progressBar -> progressBar.setIndeterminate(indeterminate));
    }

    /**
     * Creates a new {@link Component} instance using {@link #build()}, applies all currently contained
     * {@link Configurer}s to it and returns it.
     * <p>
     * Then uses the given {@link ModelAccessor} to bind the {@link ProgressBar} to the given {@link Property}.
     *
     * @param <ModelType>
     *            The type of the model to whose property to bind.
     * @param binder
     *            The {@link ModelAccessor} to bind the {@link ProgressBar} with; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link ProgressBar} to; might <b>not</b> be null.
     * @return A new {@link Component} instance, fully configured and bound, never
     *         null
     */
    public <ModelType> ProgressBar bind(ModelAccessor<ModelType> binder, Property<ModelType, Double> property) {
        return bindAndConfigure(binder, property).bind();
    }

    /**
     * Uses the given {@link ModelAccessor} to bind the {@link ProgressBar} to the given {@link Property} and starts a
     * new {@link BindingBuilder} to configure that binding.
     *
     * @param <ModelType>
     *            The type of the model to whose property to bind.
     * @param binder
     *            The {@link ModelAccessor} to bind the {@link ProgressBar} with; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link ProgressBar} to; might <b>not</b> be null.
     * @return A new {@link BindingBuilder} instance, never null
     */
    public <ModelType> BindingBuilder<ProgressBar> bindAndConfigure(ModelAccessor<ModelType> binder,
                                                                    Property<ModelType, Double> property) {
        if (binder == null) {
            throw new Http901IllegalArgumentException("Cannot bind using a null binder.");
        } else if (property == null) {
            throw new Http901IllegalArgumentException("Cannot bind using a null property.");
        }
        return new BindingBuilder<>(this, c -> binder.bindConsumer(c::setValue, property));
    }

    /**
     * Creates a new {@link Component} instance using {@link #build()}, applies all currently contained
     * {@link Configurer}s to it and returns it.
     * <p>
     * Then uses the given {@link ModelAccessor} to bind the {@link ProgressBar} to the
     * given {@link Property}.
     *
     * @param <ModelType>
     *            The type of the model to whose property to bind.
     * @param <PropertyValueType>
     *            The type of the properties' value to convert from/to.
     * @param binder
     *            The {@link ModelAccessor} to bind the {@link ProgressBar} with; might <b>not</b> be null.
     * @param converter
     *            The {@link Converter} to use to convert between the value type of the {@link ProgressBar} and the
     *            {@link Property}; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link ProgressBar} to; might <b>not</b> be null.
     * @return A new {@link Component} instance, fully configured and bound, never
     *         null
     */
    public <ModelType, PropertyValueType> ProgressBar bind(ModelAccessor<ModelType> binder,
                                                           Converter<Double, PropertyValueType> converter,
                                                           Property<ModelType, PropertyValueType> property) {
        return bindAndConfigure(binder, converter, property).bind();
    }

    /**
     * Uses the given {@link ModelAccessor} to bind the {@link ProgressBar} to the given {@link Property} and starts a
     * new {@link BindingBuilder} to configure that binding.
     *
     * @param <ModelType>
     *            The type of the model to whose property to bind.
     * @param <PropertyValueType>
     *            The type of the properties' value to convert from/to.
     * @param binder
     *            The {@link ModelAccessor} to bind the {@link ProgressBar} with; might <b>not</b> be null.
     * @param converter
     *            The {@link Converter} to use to convert between the value type of the {@link ProgressBar} and the
     *            {@link Property}; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link ProgressBar} to; might <b>not</b> be null.
     * @return A new {@link BindingBuilder} instance, never null
     */
    public <ModelType, PropertyValueType> BindingBuilder<ProgressBar> bindAndConfigure(ModelAccessor<ModelType> binder,
                                                                                       Converter<Double, PropertyValueType> converter,
                                                                                       Property<ModelType, PropertyValueType> property) {
        if (binder == null) {
            throw new Http901IllegalArgumentException("Cannot bind using a null binder.");
        } else if (converter == null) {
            throw new Http901IllegalArgumentException("Cannot bind using a null converter.");
        } else if (property == null) {
            throw new Http901IllegalArgumentException("Cannot bind using a null property.");
        }
        return new BindingBuilder<>(this, c -> binder.bindConsumer(c::setValue, converter, property));
    }
}
