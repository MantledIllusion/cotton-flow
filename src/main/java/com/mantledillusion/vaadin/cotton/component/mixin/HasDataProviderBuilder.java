package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.InMemoryDataProviderBinding;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link ComponentBuilder} for {@link HasDataProvider} implementing {@link Component}s.
 *
 * @param <C> The {@link Component} type implementing {@link HasDataProvider}.
 * @param <E> The element type of the {@link HasDataProvider}.
 * @param <F> The type of {@link ConfigurableFilter} that is used to filter the {@link DataProvider}.
 * @param <B> The final implementation type of {@link HasValueBuilder}.
 */
public interface HasDataProviderBuilder<C extends HasDataProvider<E>, E, F extends HasDataProviderBuilder.ConfigurableFilter<E>,
        B extends HasDataProviderBuilder<C, E, F, B>> extends ComponentBuilder<C, B> {

    /**
     * A configurable filter for elements of a {@link DataProvider}.
     * <p>
     *
     * @param <E> The element type of the {@link DataProvider}.
     */
    interface ConfigurableFilter<E> extends SerializablePredicate<E> {

        /**
         * A listener for changes to the configuration of {@link ConfigurableFilter}s.
         */
        @FunctionalInterface
        interface ConfigurationChangedListener {

            /**
             * Is called when the configuration of a {@link ConfigurableFilter} changes.
             */
            void configurationChanged();
        }

        /**
         * Adds a {@link ConfigurationChangedListener} to this filter which is called every time the filter changes.
         *
         * @param listener The listener to add; might <b>not</b> be null.
         * @return A {@link Registration} that can be used to remove the listener later on, never null
         */
        Registration addConfigurationChangedListener(ConfigurationChangedListener listener);

        /**
         * Tests whether the given element matches the filter.
         * <p>
         * Note that this method is only used by {@link com.vaadin.flow.data.provider.InMemoryDataProvider}s, which is
         * why it is implemented by default, accepting all elements.
         *
         * @param e The element to test; might be null.
         * @return True if the element passes this filter, false otherwise
         */
        @Override
        default boolean test(E e) {
            return true;
        }
    }

    /**
     * A basic {@link ConfigurableFilter} that just implements {@link ConfigurableFilter}'s listener logic.
     *
     * @param <E> The element type.
     */
    abstract class BasicConfigurableFilter<E> implements ConfigurableFilter<E> {

        private final List<ConfigurationChangedListener> listeners = new ArrayList<>();

        @Override
        public Registration addConfigurationChangedListener(ConfigurationChangedListener listener) {
            this.listeners.add(listener);
            return () -> this.listeners.remove(listener);
        }

        /**
         * Notify all {@link ConfigurationChangedListener}s that the configuration has changed.
         */
        protected void notifyConfigurationChanged() {
            this.listeners.forEach(ConfigurationChangedListener::configurationChanged);
        }
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
     *            The {@link ConfigurableFilter} to use; might be null.
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
     *            A {@link Supplier} of {@link ConfigurableFilter}s to use; might be null.
     * @return A new {@link HasDataProvider} instance, fully configured and bound, never null
     */
    default <ModelType> B setDataProvider(ModelAccessor<ModelType> binder, Property<ModelType, E> property, Supplier<F> filterSupplier) {
        return configure(hasDataProvider -> {
            F filter = filterSupplier != null ? filterSupplier.get() : null;
            set(ConfigurableFilter.class, filter);
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
     *            The {@link ConfigurableFilter} to use; might <b>not</b> be null.
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
     *            A {@link Supplier} of {@link ConfigurableFilter}s to use; might be null.
     * @return this
     */
    default B setDataProvider(DataProvider<E, F> dataProvider, Supplier<F> filterSupplier) {
        return configure(hasDataProvider -> {
            F filter = filterSupplier != null ?  filterSupplier.get() : null;
            set(ConfigurableFilter.class, filter);
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
     *            The {@link ConfigurableFilter} to use; might be null.
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
     *            A {@link Supplier} of {@link ConfigurableFilter}s to use; might be null.
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
            set(ConfigurableFilter.class, filter);
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
