package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link EntityBuilder} for {@link HasDataProvider} implementing {@link Component}s.
 *
 * @param <C> The {@link Component} type implementing {@link HasDataProvider}.
 * @param <E> The element type of the {@link HasDataProvider}.
 * @param <F> The type of {@link ConfigurableFilter} that is used to filter the {@link DataProvider}.
 * @param <B> The final implementation type of {@link HasValueBuilder}.
 */
public interface HasDataProviderBuilder<C extends HasDataProvider<E>, E, F extends HasDataProviderBuilder.ConfigurableFilter<E>,
        B extends HasDataProviderBuilder<C, E, F, B>> extends EntityBuilder<C, B> {

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
     * A basic {@link ConfigurableFilter} that allows saving and retrieving a filter configuration using a {@link Map}.
     *
     * @param <E> The element type.
     */
    class MappedConfigurableFilter<E> extends BasicConfigurableFilter<E> {

        private final Map<String, Object> filter = new HashMap<>();

        /**
         * Returns if the value of the given key is set.
         *
         * @param key The key; might be null.
         * @return True if the value is set, false otherwise
         */
        public boolean contains(String key) {
            return this.filter.containsKey(key);
        }

        /**
         * Returns the value to the given key.
         *
         * @param <V> The key's value type.
         * @param key The key whose value to retrieve; might be null.
         * @return The value, might be null if the key is not set or set to null
         */
        public <V> V get(String key) {
            return (V) this.filter.get(key);
        }

        /**
         * Sets the value to the given key.
         *
         * @param <V> The key's value type.
         * @param key The key whose value to set; might be null.
         * @param value The value to set; might be null.
         */
        public <V> void set(String key, V value) {
            this.filter.put(key, value);
            notifyConfigurationChanged();
        }
    }

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
}
