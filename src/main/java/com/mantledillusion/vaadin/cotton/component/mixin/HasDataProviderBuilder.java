package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.Converter;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.List;

/**
 * {@link ComponentBuilder} for {@link HasDataProvider} implementing
 * {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasDataProvider}.
 * @param <E>
 *            The element type of the {@link HasDataProvider}.
 * @param <B>
 *            The final implementation type of {@link HasDataProviderBuilder}.
 */
public interface HasDataProviderBuilder<C extends HasDataProvider<E>, E, B extends HasDataProviderBuilder<C, E, B>>
		extends ComponentBuilder<C, B> {

	/**
	 * Builder method, configures a {@link DataProvider} that is receiving its values from a binding to a {@link Property}.
	 * 
	 * @see HasDataProvider#setDataProvider(DataProvider)
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link DataProvider} with; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link DataProvider} to; might <b>not</b> be null.
	 * @return this
	 */
	default <ModelType> B setDataProvider(ModelAccessor<ModelType> binder, Property<ModelType, List<E>> property) {
		if (binder == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null binder.");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property.");
		}
		return configure(hasDataProvider -> hasDataProvider.setDataProvider(binder.provide(property)));
	}

	/**
	 * Builder method, configures a {@link DataProvider} that is receiving its values from a binding to a {@link Property}.
	 * 
	 * @see HasDataProvider#setDataProvider(DataProvider)
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param <PropertyValueType>
	 *            The type of the properties' value to convert from/to.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link DataProvider} with; might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use to convert between the value type of the {@link DataProvider} and the
	 *            {@link Property}; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link DataProvider} to; might <b>not</b> be null.
	 * @return this
	 */
	default <ModelType, PropertyValueType> B setDataProvider(ModelAccessor<ModelType> binder,
															 Converter<E, PropertyValueType> converter,
															 Property<ModelType, List<PropertyValueType>> property) {
		if (binder == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null binder.");
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null converter.");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property.");
		}
		return configure(hasDataProvider -> hasDataProvider.setDataProvider(binder.provide(converter, property)));
	}
}
