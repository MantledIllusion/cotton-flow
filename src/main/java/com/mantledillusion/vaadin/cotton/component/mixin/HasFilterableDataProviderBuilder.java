package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.data.epiphy.interfaces.type.ListedProperty;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.Converter;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.HasFilterableDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializablePredicate;

/**
 * {@link ComponentBuilder} for {@link HasFilterableDataProvider} implementing
 * {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasFilterableDataProvider}.
 * @param <E>
 *            The element type of the {@link HasFilterableDataProvider}.
 * @param <E>
 *            The filter type of the {@link HasFilterableDataProvider}.
 * @param <B>
 *            The final implementation type of
 *            {@link HasFilterableDataProviderBuilder}.
 */
public interface HasFilterableDataProviderBuilder<C extends HasFilterableDataProvider<E, F>, E, F, B extends HasFilterableDataProviderBuilder<C, E, F, B>>
		extends ComponentBuilder<C, B> {

	/**
	 * Builder method, configures a {@link DataProvider} that is receiving its
	 * values from a binding to a {@link ListedProperty}.
	 * 
	 * @see HasFilterableDataProvider#setDataProvider(DataProvider,
	 *      SerializableFunction)
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link DataProvider} with;
	 *            might <b>not</b> be null.
	 * @param property
	 *            The {@link ListedProperty} to bind the {@link DataProvider} to;
	 *            might <b>not</b> be null.
	 * @param filterConverter
	 *            a function that converts filter values produced by this listing
	 *            into filter values expected by the provided data provider; might
	 *            <b>not</b> be null.
	 * @return this
	 */
	default <ModelType> B setDataProvider(ModelAccessor<ModelType> binder, ListedProperty<ModelType, E> property,
			SerializableFunction<F, SerializablePredicate<E>> filterConverter) {
		if (binder == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null binder.");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property.");
		}
		return configure(hasDataProvider -> hasDataProvider.setDataProvider(binder.provide(property), filterConverter));
	}

	/**
	 * Builder method, configures a {@link DataProvider} that is receiving its
	 * values from a binding to a {@link ListedProperty}.
	 * 
	 * @see HasFilterableDataProvider#setDataProvider(DataProvider,
	 *      SerializableFunction)
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param <PropertyValueType>
	 *            The type of the properties' value to convert from/to.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link DataProvider} with;
	 *            might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use to convert between the value type of
	 *            the {@link DataProvider} and the {@link ListedProperty}; might
	 *            <b>not</b> be null.
	 * @param property
	 *            The {@link ListedProperty} to bind the {@link DataProvider} to;
	 *            might <b>not</b> be null.
	 * @param filterConverter
	 *            a function that converts filter values produced by this listing
	 *            into filter values expected by the provided data provider; might
	 *            <b>not</b> be null.
	 * @return this
	 */
	default <ModelType, PropertyValueType> B setDataProvider(ModelAccessor<ModelType> binder,
			Converter<E, PropertyValueType> converter, ListedProperty<ModelType, PropertyValueType> property,
			SerializableFunction<F, SerializablePredicate<E>> filterConverter) {
		if (binder == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null binder.");
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null converter.");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property.");
		}
		return configure(hasDataProvider -> hasDataProvider.setDataProvider(binder.provide(converter, property),
				filterConverter));
	}
}
