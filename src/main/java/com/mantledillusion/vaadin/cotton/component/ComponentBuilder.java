package com.mantledillusion.vaadin.cotton.component;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.component.mixin.BindingBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;

import java.util.function.BiConsumer;

/**
 * Interface for {@link Component} builders.
 *
 * @param <C>
 *            The {@link Component} type this builder builds. Not an extension
 *            of {@link Component} on purpose, since Vaadin handles shared
 *            {@link Component} behavior using interfaces that are not
 *            necessarily bound to {@link Component}s.
 * @param <B>
 *            The final implementation type of this {@link ComponentBuilder}.
 *            Necessary to allow builder methods of non-final implementations to
 *            return the builder instance in the correct type.
 */
public interface ComponentBuilder<C, B extends ComponentBuilder<C, B>> extends EntityBuilder<C, B> {

    /**
     * Creates a new {@link Component} instance, applies all currently contained {@link Configurer}s to it and returns it.
     *
     * @return A new {@link Component} instance, fully configured, never null
     */
    C build();

    /**
     * Uses the given {@link ModelAccessor} to bind the {@link Component} to the given {@link Property} and starts a
     * new {@link BindingBuilder} to configure that binding.
     *
     * @param <ModelType>
     *            The type of the model to whose property to bind.
     * @param binder
     *            The {@link ModelAccessor} to bind the {@link Component} with; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link Component} to; might <b>not</b> be null.
     * @return A new {@link BindingBuilder} instance, never null
     */
    default <ModelType, ValueType> C bind(ModelAccessor<ModelType> binder,
                                          Property<ModelType, ValueType> property,
                                          BiConsumer<C, ValueType> valueSetter) {
        return bindAndConfigure(binder, property, valueSetter).bind();
    }

    /**
     * Uses the given {@link ModelAccessor} to bind the {@link Component} to the given {@link Property} and starts a
     * new {@link BindingBuilder} to configure that binding.
     *
     * @param <ModelType>
     *          The type of the model to whose property to bind.
     * @param binder
     *          The {@link ModelAccessor} to bind the {@link Component} with; might <b>not</b> be null.
     * @param property
     *          The {@link Property} to bind the {@link Component} to; might <b>not</b> be null.
     * @param valueSetter
     *          A {@link BiConsumer} supposed to set the value in the {@link Component}; might <b>not</b> be null.
     * @return A new {@link BindingBuilder} instance, never null
     */
    default <ModelType, ValueType> BindingBuilder<C> bindAndConfigure(ModelAccessor<ModelType> binder,
                                                                      Property<ModelType, ValueType> property,
                                                                      BiConsumer<C, ValueType> valueSetter) {
        if (binder == null) {
            throw new Http901IllegalArgumentException("Cannot bind using a null binder.");
        } else if (property == null) {
            throw new Http901IllegalArgumentException("Cannot bind using a null property.");
        } else if (valueSetter == null) {
            throw new Http901IllegalArgumentException("Cannot bind using a null valueSetter.");
        }
        return new BindingBuilder<>(this, c -> binder.bindConsumer(valueType -> valueSetter.accept(c, valueType), property));
    }
}