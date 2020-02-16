package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.Converter;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * {@link ComponentBuilder} for {@link HasValue} implementing
 * {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasValue}.
 * @param <V>
 *            The value type of the {@link HasValue}.
 * @param <B>
 *            The final implementation type of {@link HasValueBuilder}.
 */
public interface HasValueBuilder<C extends HasValue<?, V>, V, B extends HasValueBuilder<C, V, B>>
		extends ComponentBuilder<C, B> {

	/**
	 * Builder method, configures whether the {@link Component} is read-only, so its
	 * value cannot be changed.
	 * 
	 * @see HasValue#setReadOnly(boolean)
	 * @param readOnly
	 *            True if the {@link Component} has to be read-only, false
	 *            otherwise.
	 * @return this
	 */
	default B setReadOnly(boolean readOnly) {
		return configure(hasValue -> hasValue.setReadOnly(readOnly));
	}

	/**
	 * Builder method, configures whether the required indicator should be visible.
	 * 
	 * @see HasValue#setRequiredIndicatorVisible(boolean)
	 * @param requiredIndicatorVisible
	 *            True if the indicator has to be visible, false otherwise.
	 * @return this
	 */
	default B setRequiredIndicator(boolean requiredIndicatorVisible) {
		return configure(hasValue -> hasValue.setRequiredIndicatorVisible(requiredIndicatorVisible));
	}

	/**
	 * Builder method, configures the initial value of the component after building.
	 * 
	 * @see HasValue#setValue(Object)
	 * @param value
	 *            The initial value; might be null.
	 * @return this
	 */
	default B setValue(V value) {
		return configure(hasValue -> hasValue.setValue(value));
	}

	/**
	 * Creates a new {@link HasValue} instance using {@link #build()}, applies all currently contained
	 * {@link Configurer}s to it and returns it.
	 * <p>
	 * Then uses the given {@link ModelAccessor} to bind the {@link HasValue} to the given {@link Property}.
	 * 
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link HasValue} with; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link HasValue} to; might <b>not</b> be null.
	 * @return A new {@link HasValue} instance, fully configured and bound, never null
	 */
	default <ModelType> C bind(ModelAccessor<ModelType> binder, Property<ModelType, V> property) {
		return bindAndConfigure(binder, property).bind();
	}

	/**
	 * Uses the given {@link ModelAccessor} to bind the {@link HasValue} to the given {@link Property} and starts a
	 * new {@link BindingBuilder} to configure that binding.
	 *
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link HasValue} with; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link HasValue} to; might <b>not</b> be null.
	 * @return A new {@link BindingBuilder} instance, never null
	 */
	default <ModelType> BindingBuilder<C> bindAndConfigure(ModelAccessor<ModelType> binder,
														   Property<ModelType, V> property) {
		if (binder == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null binder.");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property.");
		}
		return new BindingBuilder<>(this, c -> binder.bindHasValue(c, property));
	}

	/**
	 * Creates a new {@link HasValue} instance using {@link #build()}, applies all currently contained
	 * {@link Configurer}s to it and returns it.
	 * <p>
	 * Then uses the given {@link ModelAccessor} to bind the {@link HasValue} to the given {@link Property}.
	 * 
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param <PropertyValueType>
	 *            The type of the properties' value to convert from/to.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link HasValue} with; might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use to convert between the value type of the {@link HasValue} and the
	 *            {@link Property}; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link HasValue} to; might <b>not</b> be null.
	 * @return A new {@link HasValue} instance, fully configured and bound, never null
	 */
	default <ModelType, PropertyValueType> C bind(ModelAccessor<ModelType> binder,
												  Converter<V, PropertyValueType> converter,
												  Property<ModelType, PropertyValueType> property) {
		return bindAndConfigure(binder, converter, property).bind();
	}

	/**
	 * Uses the given {@link ModelAccessor} to bind the {@link HasValue} to the given {@link Property} and starts a
	 * new {@link BindingBuilder} to configure that binding.
	 *
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param <PropertyValueType>
	 *            The type of the properties' value to convert from/to.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link HasValue} with; might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use to convert between the value type of the {@link HasValue} and the
	 *            {@link Property}; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link HasValue} to; might <b>not</b> be null.
	 * @return A new {@link BindingBuilder} instance, never null
	 */
	default <ModelType, PropertyValueType> BindingBuilder<C> bindAndConfigure(ModelAccessor<ModelType> binder,
																			  Converter<V, PropertyValueType> converter,
																			  Property<ModelType, PropertyValueType> property) {
		if (binder == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null binder.");
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null converter.");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property.");
		}
		return new BindingBuilder<>(this, c -> binder.bindHasValue(c, converter, property));
	}
}
