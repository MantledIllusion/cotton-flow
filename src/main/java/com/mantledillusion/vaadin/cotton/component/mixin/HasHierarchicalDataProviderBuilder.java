package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.provider.hierarchy.HasHierarchicalDataProvider;

/**
 * {@link ComponentBuilder} for {@link HasHierarchicalDataProvider} implementing {@link Component}s.
 *
 * @param <C> The {@link Component} type implementing {@link HasHierarchicalDataProvider}.
 * @param <E> The element type of the {@link HasHierarchicalDataProvider}.
 * @param <B> The final implementation type of {@link HasValueBuilder}.
 */
public interface HasHierarchicalDataProviderBuilder<C extends HasHierarchicalDataProvider<E>, E, B extends HasHierarchicalDataProviderBuilder<C, E, B>> 
        extends ComponentBuilder<C, B> {

    /**
     * Creates a new {@link HasHierarchicalDataProvider} instance using {@link #build()}, applies all currently
     * contained {@link Configurer}s to it and returns it.
     * <p>
     * Then uses the given {@link ModelAccessor} to bind the {@link HasHierarchicalDataProvider} to the given
     * {@link Property}.
     *
     * @param <ModelType> The type of the model to whose property to bind.
     * @param binder The {@link ModelAccessor} to bind the {@link HasHierarchicalDataProvider} with; might <b>not</b>
     *               be null.
     * @param property The {@link Property} to bind the {@link HasHierarchicalDataProvider} to; might <b>not</b> be
     *                 null.
     * @return A new {@link HasHierarchicalDataProvider} instance, fully configured and bound, never null
     */
    default <ModelType> B bind(ModelAccessor<ModelType> binder, Property<ModelType, E> property) {
        return configure(grid -> binder.bindHasHierarchicalDataProvider(grid, property));
    }
}
