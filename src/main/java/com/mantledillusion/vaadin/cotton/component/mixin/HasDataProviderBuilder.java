package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.HasDataProvider;

/**
 * {@link ComponentBuilder} for {@link HasDataProvider} implementing {@link Component}s.
 *
 * @param <C> The {@link Component} type implementing {@link HasDataProvider}.
 * @param <E> The element type of the {@link HasDataProvider}.
 * @param <B> The final implementation type of {@link HasValueBuilder}.
 */
public interface HasDataProviderBuilder<C extends HasDataProvider<E>, E, B extends HasDataProviderBuilder<C, E, B>>
        extends ComponentBuilder<C, B> {

    /**
     * Creates a new {@link HasDataProvider} instance using {@link #build()}, applies all currently contained
     * {@link Configurer}s to it and returns it.
     * <p>
     * Then uses the given {@link ModelAccessor} to bind the {@link HasDataProvider} to the given {@link Property}.
     *
     * @param <ModelType>
     *            The type of the model to whose property to bind.
     * @param binder
     *            The {@link ModelAccessor} to bind the {@link HasDataProvider} with; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link HasDataProvider} to; might <b>not</b> be null.
     * @return A new {@link HasDataProvider} instance, fully configured and bound, never null
     */
    default <ModelType> B setDataProvider(ModelAccessor<ModelType> binder, Property<ModelType, E> property) {
        return configure(hasDataProvider -> binder.bindHasDataProvider(hasDataProvider, property));
    }
}
