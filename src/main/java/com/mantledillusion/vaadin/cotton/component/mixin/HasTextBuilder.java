package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.model.Converter;
import com.mantledillusion.vaadin.cotton.model.ModelAccessor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;

/**
 * {@link ComponentBuilder} for {@link HasText} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasText}.
 * @param <B>
 *            The final implementation type of {@link HasTextBuilder}.
 */
public interface HasTextBuilder<C extends HasText, B extends HasTextBuilder<C, B>> extends ComponentBuilder<C, B> {

	/**
	 * Builder method, configures the initial text of the component after building.
	 * 
	 * @see HasText#setText(String)
	 * @param msgId
	 *            The initial text, or a message id to localize; might be null.
	 * @return this
	 */
	default B setValue(String msgId) {
		return configure(hasValue -> hasValue.setText(WebEnv.getTranslation(msgId)));
	}

	/**
	 * Creates a new {@link Component} instance using {@link #build()}, applies all
	 * currently contained {@link Configurer}s to it and returns it.
	 * <p>
	 * Then uses the given {@link ModelAccessor} to bind the {@link HasText} to the
	 * given {@link Property}.
	 * 
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link HasText} with; might
	 *            <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link HasText} to; might
	 *            <b>not</b> be null.
	 * @return A new {@link Component} instance, fully configured and bound, never
	 *         null
	 */
	default <ModelType> C bind(ModelAccessor<ModelType> binder, Property<ModelType, String> property) {
		return bindAndConfigure(binder, property).bind();
	}

	/**
	 * Uses the given {@link ModelAccessor} to bind the {@link HasText} to the given {@link Property} and starts a
	 * new {@link BindingBuilder} to configure that binding.
	 *
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link HasText} with; might
	 *            <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link HasText} to; might
	 *            <b>not</b> be null.
	 * @return A new {@link BindingBuilder} instance, never null
	 */
	default <ModelType> BindingBuilder<C> bindAndConfigure(ModelAccessor<ModelType> binder, Property<ModelType, String> property) {
		if (binder == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null binder.");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property.");
		}
		return new BindingBuilder<>(this, c -> binder.bindConsumer(text -> c.setText(text), property));
	}

	/**
	 * Creates a new {@link Component} instance using {@link #build()}, applies all
	 * currently contained {@link Configurer}s to it and returns it.
	 * <p>
	 * Then uses the given {@link ModelAccessor} to bind the {@link HasText} to the
	 * given {@link Property}.
	 * 
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param <PropertyValueType>
	 *            The type of the properties' value to convert from/to.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link HasText} with; might
	 *            <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use to convert between the value type of
	 *            the {@link HasText} and the {@link Property}; might
	 *            <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link HasText} to; might
	 *            <b>not</b> be null.
	 * @return A new {@link Component} instance, fully configured and bound, never
	 *         null
	 */
	default <ModelType, PropertyValueType> C bind(ModelAccessor<ModelType> binder,
												  Converter<String, PropertyValueType> converter,
												  Property<ModelType, PropertyValueType> property) {
		return bindAndConfigure(binder, converter, property).bind();
	}

	/**
	 * Uses the given {@link ModelAccessor} to bind the {@link HasText} to the given {@link Property} and starts a
	 * new {@link BindingBuilder} to configure that binding.
	 *
	 * @param <ModelType>
	 *            The type of the model to whose property to bind.
	 * @param <PropertyValueType>
	 *            The type of the properties' value to convert from/to.
	 * @param binder
	 *            The {@link ModelAccessor} to bind the {@link HasText} with; might
	 *            <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use to convert between the value type of
	 *            the {@link HasText} and the {@link Property}; might
	 *            <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind the {@link HasText} to; might
	 *            <b>not</b> be null.
	 * @return A new {@link BindingBuilder} instance, never null
	 */
	default <ModelType, PropertyValueType> BindingBuilder<C> bindAndConfigure(ModelAccessor<ModelType> binder,
																			  Converter<String, PropertyValueType> converter,
																			  Property<ModelType, PropertyValueType> property) {
		if (binder == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null binder.");
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null converter.");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property.");
		}
		return new BindingBuilder<>(this, c -> binder.bindConsumer(text -> c.setText(text), converter, property));
	}
}
