package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.mantledillusion.vaadin.cotton.model.InMemoryDataProviderBinding;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.provider.hierarchy.HasHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;

import java.util.function.Supplier;

/**
 * {@link EntityBuilder} for {@link HasHierarchicalDataProvider} implementing {@link Component}s.
 *
 * @param <C> The {@link Component} type implementing {@link HasHierarchicalDataProvider}.
 * @param <E> The element type of the {@link HasHierarchicalDataProvider}.
 * @param <B> The final implementation type of {@link HasValueBuilder}.
 */
public interface HasHierarchicalDataProviderBuilder<C extends HasHierarchicalDataProvider<E>, E,
        F extends HasDataProviderBuilder.ConfigurableFilter<E>,
        B extends HasHierarchicalDataProviderBuilder<C, E, F, B>>
        extends HasDataProviderBuilder<C, E, F, B>, EntityBuilder<C, B> {

    /**
     * Creates a new {@link HasHierarchicalDataProvider} instance, applies all currently contained {@link Configurer}s
     * to it and returns it.
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
    default <ModelType> B setDataProvider(ModelAccessor<ModelType> binder, Property<ModelType, E> property) {
        return configure(hasDataProvider -> binder.bindHasHierarchicalDataProvider(hasDataProvider, property));
    }

    /**
     * Builder method, configures a new {@link com.vaadin.flow.data.provider.InMemoryDataProvider} bound to the given
     * {@link Property} using the given {@link ModelAccessor}.
     *
     * @param <ModelType>
     *            The type of the model to whose property to bind.
     * @param binder
     *            The {@link ModelAccessor} to bind the {@link HasHierarchicalDataProvider} with; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link HasHierarchicalDataProvider} to; might <b>not</b> be null.
     * @param filter
     *            The {@link HasDataProviderBuilder.ConfigurableFilter} to use; might be null.
     * @return A new {@link HasHierarchicalDataProvider} instance, fully configured and bound, never null
     */
    default <ModelType> B setDataProvider(ModelAccessor<ModelType> binder, Property<ModelType, E> property, F filter) {
        return setDataProvider(binder, property, () -> filter);
    }

    /**
     * Builder method, configures a new {@link com.vaadin.flow.data.provider.InMemoryDataProvider} bound to the given
     * {@link Property} using the given {@link ModelAccessor}.
     *
     * @param <ModelType>
     *            The type of the model to whose property to bind.
     * @param binder
     *            The {@link ModelAccessor} to bind the {@link HasHierarchicalDataProvider} with; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link HasHierarchicalDataProvider} to; might <b>not</b> be null.
     * @param filterSupplier
     *            A {@link Supplier} of {@link HasDataProviderBuilder.ConfigurableFilter}s to use; might be null.
     * @return A new {@link HasHierarchicalDataProvider} instance, fully configured and bound, never null
     */
    default <ModelType> B setDataProvider(ModelAccessor<ModelType> binder, Property<ModelType, E> property, Supplier<F> filterSupplier) {
        return configure(hasDataProvider -> {
            F filter = filterSupplier != null ? filterSupplier.get() : null;
            set(HasDataProviderBuilder.ConfigurableFilter.class, filter);
            InMemoryDataProviderBinding<E> binding = binder.bindHasHierarchicalDataProvider(hasDataProvider, property, filter);
            if (filter != null) {
                filter.addConfigurationChangedListener(() -> binding.getDataProvider().refreshAll());
            }
        }, true);
    }

    /**
     * Builder method, configures the given {@link HierarchicalDataProvider}.
     *
     * @param dataProvider
     *            The {@link HierarchicalDataProvider} to configure; might <b>not</b> be null.
     * @return this
     */
    default B setDataProvider(HierarchicalDataProvider<E, ?> dataProvider) {
        return configure(hasDataProvider -> hasDataProvider.setDataProvider(dataProvider), true);
    }

    /**
     * Builder method, configures the given {@link HierarchicalDataProvider}.
     *
     * @param dataProvider
     *            The {@link HierarchicalDataProvider} to configure; might <b>not</b> be null.
     * @param filter
     *            The {@link HasDataProviderBuilder.ConfigurableFilter} to use; might <b>not</b> be null.
     * @return this
     */
    default B setDataProvider(HierarchicalDataProvider<E, F> dataProvider, F filter) {
        return setDataProvider(dataProvider, () -> filter);
    }

    /**
     * Builder method, configures the given {@link HierarchicalDataProvider}.
     *
     * @param dataProvider
     *            The {@link HierarchicalDataProvider} to configure; might <b>not</b> be null.
     * @param filterSupplier
     *            A {@link Supplier} of {@link HasDataProviderBuilder.ConfigurableFilter}s to use; might be null.
     * @return this
     */
    default B setDataProvider(HierarchicalDataProvider<E, F> dataProvider, Supplier<F> filterSupplier) {
        return configure(hasDataProvider -> {
            F filter = filterSupplier != null ?  filterSupplier.get() : null;
            set(HasDataProviderBuilder.ConfigurableFilter.class, filter);
            if (filter != null) {
                HierarchicalConfigurableFilterDataProvider<E, Void, F> configurableFilterDataProvider =
                        dataProvider.withConfigurableFilter();
                configurableFilterDataProvider.setFilter(filter);
                filter.addConfigurationChangedListener(configurableFilterDataProvider::refreshAll);
                hasDataProvider.setDataProvider(configurableFilterDataProvider);
            } else {
                hasDataProvider.setDataProvider(dataProvider);
            }
        }, true);
    }
}
