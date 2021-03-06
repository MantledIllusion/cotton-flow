package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.InMemoryDataProviderBinding;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * {@link EntityBuilder} for {@link HasDataProvider} implementing {@link Component}s.
 *
 * @param <C> The {@link Component} type implementing {@link HasDataProvider}.
 * @param <E> The element type of the {@link HasDataProvider}.
 * @param <F> The type of {@link HasDataProviderBuilder.ConfigurableFilter} that is used to filter the {@link DataProvider}.
 * @param <B> The final implementation type of {@link HasValueBuilder}.
 */
public interface HasSimpleDataProviderBuilder<C extends HasDataProvider<E>, E,
        F extends HasDataProviderBuilder.ConfigurableFilter<E>,
        B extends HasSimpleDataProviderBuilder<C, E, F, B>>
        extends HasDataProviderBuilder<C, E, F, B>, EntityBuilder<C, B> {

    /**
     * Builder method, configures a new {@link com.vaadin.flow.data.provider.InMemoryDataProvider} bound to the given
     * {@link Property} using the given {@link ModelAccessor}.
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
        return configure(hasDataProvider -> binder.bindHasDataProvider(hasDataProvider, property, null));
    }

    /**
     * Builder method, configures a new {@link com.vaadin.flow.data.provider.InMemoryDataProvider} bound to the given
     * {@link Property} using the given {@link ModelAccessor}.
     *
     * @param <ModelType>
     *            The type of the model to whose property to bind.
     * @param binder
     *            The {@link ModelAccessor} to bind the {@link HasDataProvider} with; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link HasDataProvider} to; might <b>not</b> be null.
     * @param filter
     *            The {@link HasDataProviderBuilder.ConfigurableFilter} to use; might be null.
     * @return A new {@link HasDataProvider} instance, fully configured and bound, never null
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
     *            The {@link ModelAccessor} to bind the {@link HasDataProvider} with; might <b>not</b> be null.
     * @param property
     *            The {@link Property} to bind the {@link HasDataProvider} to; might <b>not</b> be null.
     * @param filterSupplier
     *            A {@link Supplier} of {@link HasDataProviderBuilder.ConfigurableFilter}s to use; might be null.
     * @return A new {@link HasDataProvider} instance, fully configured and bound, never null
     */
    default <ModelType> B setDataProvider(ModelAccessor<ModelType> binder, Property<ModelType, E> property, Supplier<F> filterSupplier) {
        return configure(hasDataProvider -> {
            F filter = filterSupplier != null ? filterSupplier.get() : null;
            set(HasDataProviderBuilder.ConfigurableFilter.class, filter);
            InMemoryDataProviderBinding<E> binding = binder.bindHasDataProvider(hasDataProvider, property, filter);
            if (filter != null) {
                filter.addConfigurationChangedListener(() -> binding.getDataProvider().refreshAll());
            }
        }, true);
    }

    /**
     * Builder method, configures the given {@link DataProvider}.
     *
     * @param dataProvider
     *            The {@link DataProvider} to configure; might <b>not</b> be null.
     * @return this
     */
    default B setDataProvider(DataProvider<E, ?> dataProvider) {
        return configure(hasDataProvider -> hasDataProvider.setDataProvider(dataProvider), true);
    }

    /**
     * Builder method, configures the given {@link DataProvider}.
     *
     * @param dataProvider
     *            The {@link DataProvider} to configure; might <b>not</b> be null.
     * @param filter
     *            The {@link HasDataProviderBuilder.ConfigurableFilter} to use; might <b>not</b> be null.
     * @return this
     */
    default B setDataProvider(DataProvider<E, F> dataProvider, F filter) {
        return setDataProvider(dataProvider, () -> filter);
    }

    /**
     * Builder method, configures the given {@link DataProvider}.
     *
     * @param dataProvider
     *            The {@link DataProvider} to configure; might <b>not</b> be null.
     * @param filterSupplier
     *            A {@link Supplier} of {@link HasDataProviderBuilder.ConfigurableFilter}s to use; might be null.
     * @return this
     */
    default B setDataProvider(DataProvider<E, F> dataProvider, Supplier<F> filterSupplier) {
        return configure(hasDataProvider -> {
            F filter = filterSupplier != null ?  filterSupplier.get() : null;
            set(HasDataProviderBuilder.ConfigurableFilter.class, filter);
            if (filter != null) {
                ConfigurableFilterDataProvider<E, Void, F> configurableFilterDataProvider =
                        dataProvider.withConfigurableFilter();
                configurableFilterDataProvider.setFilter(filter);
                filter.addConfigurationChangedListener(configurableFilterDataProvider::refreshAll);
                hasDataProvider.setDataProvider(configurableFilterDataProvider);
            } else {
                hasDataProvider.setDataProvider(dataProvider);
            }
        }, true);
    }

    /**
     * Builder method, configures a new {@link CallbackDataProvider} that uses the given callbacks.
     *
     * @param countCallback
     *            The {@link CallbackDataProvider.CountCallback} that is able to determine the count of all elements
     *            matching the query; might <b>not</b> be null.
     * @param fetchCallback
     *            The {@link CallbackDataProvider.FetchCallback} that is able to fetch all elements matching the
     *            query; might <b>not</b> be null.
     * @return A new {@link HasDataProvider} instance, fully configured and bound, never null
     */
    default B setDataProvider(CallbackDataProvider.CountCallback<E, Void> countCallback,
                              CallbackDataProvider.FetchCallback<E, Void> fetchCallback) {
        return configure(hasDataProvider -> hasDataProvider.setDataProvider(DataProvider.
                fromCallbacks(fetchCallback, countCallback)), true);
    }

    /**
     * Builder method, configures a new {@link CallbackDataProvider} that uses the given callbacks.
     *
     * @param countCallback
     *            The {@link CallbackDataProvider.CountCallback} that is able to determine the count of all elements
     *            matching the query; might <b>not</b> be null.
     * @param fetchCallback
     *            The {@link CallbackDataProvider.FetchCallback} that is able to fetch all elements matching the
     *            query; might <b>not</b> be null.
     * @param filter
     *            The {@link HasDataProviderBuilder.ConfigurableFilter} to use; might be null.
     * @return A new {@link HasDataProvider} instance, fully configured and bound, never null
     */
    default B setDataProvider(CallbackDataProvider.CountCallback<E, F> countCallback,
                              CallbackDataProvider.FetchCallback<E, F> fetchCallback, F filter) {
        return setDataProvider(countCallback, fetchCallback, () -> filter);
    }

    /**
     * Builder method, configures a new {@link CallbackDataProvider} that uses the given callbacks.
     *
     * @param countCallback
     *            The {@link CallbackDataProvider.CountCallback} that is able to determine the count of all elements
     *            matching the query; might <b>not</b> be null.
     * @param fetchCallback
     *            The {@link CallbackDataProvider.FetchCallback} that is able to fetch all elements matching the
     *            query; might <b>not</b> be null.
     * @param filterSupplier
     *            A {@link Supplier} of {@link HasDataProviderBuilder.ConfigurableFilter}s to use; might be null.
     * @return A new {@link HasDataProvider} instance, fully configured and bound, never null
     */
    default B setDataProvider(CallbackDataProvider.CountCallback<E, F> countCallback,
                              CallbackDataProvider.FetchCallback<E, F> fetchCallback, Supplier<F> filterSupplier) {
        if (countCallback == null) {
            throw new Http901IllegalArgumentException("Cannot configure a data provider from a null count callback.");
        } else if (fetchCallback == null) {
            throw new Http901IllegalArgumentException("Cannot configure a data provider from a null fetch callback.");
        }
        return configure(hasDataProvider -> {
            F filter = filterSupplier != null ? filterSupplier.get() : null;
            set(HasDataProviderBuilder.ConfigurableFilter.class, filter);
            CallbackDataProvider<E, F> callbackDataProvider = DataProvider.fromFilteringCallbacks(fetchCallback, countCallback);
            if (filter == null) {
                hasDataProvider.setDataProvider(callbackDataProvider);
            } else {
                ConfigurableFilterDataProvider<E, Void, F> configurableFilterDataProvider =
                        callbackDataProvider.withConfigurableFilter();
                configurableFilterDataProvider.setFilter(filter);
                filter.addConfigurationChangedListener(configurableFilterDataProvider::refreshAll);
                hasDataProvider.setDataProvider(configurableFilterDataProvider);
            }
        }, true);
    }
}
