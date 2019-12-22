package com.mantledillusion.vaadin.cotton.model;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.data.epiphy.context.Context;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;
import org.apache.commons.lang3.ObjectUtils;

import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.shared.Registration;

abstract class ModelBinder<ModelType> implements ModelHandler<ModelType> {

	private static final Procedure NOOP = () -> {};
	private static final Consumer NOCONSUME = value -> {};

	private interface Procedure {

		void trigger();
	}

	private interface Binding {

		void update(Context context, UpdateType type);

		void unbind();
	}

	enum UpdateType {
		EXCHANGE, ADD, REMOVE;
	}

	private final Context context;
	private final Map<Property<ModelType, ?>, List<Binding>> bindings = new IdentityHashMap<>();

	protected ModelBinder(Context context) {
		this.context = context;
	}

	protected Context getContext() {
		return this.context;
	}

	// ######################################################################################################################################
	// ########################################################## CONSUMER BINDING ##########################################################
	// ######################################################################################################################################

	private static final class ConsumerBinding implements Binding {

		private final Procedure valueReader;

		private ConsumerBinding(Procedure valueReader) {
			this.valueReader = valueReader;
		}

		@Override
		public void update(Context context, UpdateType type) {
			this.valueReader.trigger();
		}

		@Override
		public void unbind() {
			// Not required
		}
	}

	/**
	 * Binds the given {@link Consumer} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link Consumer} to bind.
	 * @param consumer
	 *            The {@link Consumer} to bind; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType> void bind(Consumer<FieldValueType> consumer, Property<ModelType, FieldValueType> property) {
		if (consumer == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + Consumer.class.getSimpleName());
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> consumer.accept(ModelBinder.this.get(property));

		addBinding(property, new ConsumerBinding(valueReader));
	}

	/**
	 * Binds the given {@link Consumer} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link Consumer} to bind.
	 * @param <PropertyValueType>
	 *            The value type of the property to bind with.
	 * @param consumer
	 *            The {@link Consumer} to bind; might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use for conversion between the field's
	 *            and the properties' value types; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType, PropertyValueType> void bind(Consumer<FieldValueType> consumer,
														 Converter<FieldValueType, PropertyValueType> converter,
														 Property<ModelType, PropertyValueType> property) {
		if (consumer == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + Consumer.class.getSimpleName());
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null converter");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> consumer.accept(converter.toField(ModelBinder.this.get(property)));

		addBinding(property, new ConsumerBinding(valueReader));
	}

	// ######################################################################################################################################
	// ########################################################## HASVALUE BINDING ##########################################################
	// ######################################################################################################################################

	@SuppressWarnings("rawtypes")
	private static final class HasValueBinding implements Binding, ValueChangeListener {

		private static final long serialVersionUID = 1L;

		private final Procedure valueReader;
		private final Procedure valueWriter;
		private final Procedure enablementUpdater;
		private final Registration registration;
		private boolean synchronizing = false;

		@SuppressWarnings("unchecked")
		private HasValueBinding(HasValue<?, ?> hasValue, Procedure valueReader, Procedure valueWriter,
				Procedure enablementSwitch) {
			this.valueReader = valueReader;
			this.valueWriter = valueWriter;
			this.enablementUpdater = enablementSwitch;
			this.registration = hasValue.addValueChangeListener(this);
		}

		@Override
		public synchronized void valueChanged(ValueChangeEvent event) {
			if (!this.synchronizing) {
				this.synchronizing = true;
				this.valueWriter.trigger();
				this.synchronizing = false;
			}
		}

		@Override
		public synchronized void update(Context context, UpdateType type) {
			if (!this.synchronizing) {
				this.synchronizing = true;
				this.enablementUpdater.trigger();
				this.valueReader.trigger();
				this.synchronizing = false;
			}
		}

		@Override
		public void unbind() {
			this.registration.remove();
		}
	}

	/**
	 * Binds the given {@link HasValue} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link HasValue} to bind.
	 * @param hasValue
	 *            The {@link HasValue} to bind; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType> void bind(HasValue<?, FieldValueType> hasValue,
									  Property<ModelType, FieldValueType> property) {
		if (hasValue == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + HasValue.class.getSimpleName());
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> hasValue
				.setValue(ObjectUtils.defaultIfNull(ModelBinder.this.get(property), hasValue.getEmptyValue()));
		Procedure valueWriter;
		if (property.isWritable()) {
			valueWriter = () -> ModelBinder.this.set(property, hasValue.getValue());
		} else {
			valueWriter = NOOP;
		}
		createHasValueBinding(property, hasValue, valueReader, valueWriter);
	}

	/**
	 * Binds the given {@link HasValue} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link HasValue} to bind.
	 * @param <PropertyValueType>
	 *            The value type of the property to bind with.
	 * @param hasValue
	 *            The {@link HasValue} to bind; might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use for conversion between the field's
	 *            and the properties' value types; might <b>not</b> be null.
	 * @param property
	 *            The {@link Property} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType, PropertyValueType> void bind(HasValue<?, FieldValueType> hasValue,
														 Converter<FieldValueType, PropertyValueType> converter,
														 Property<ModelType, PropertyValueType> property) {
		if (hasValue == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + HasValue.class.getSimpleName());
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null converter");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> hasValue.setValue(
				ObjectUtils.defaultIfNull(converter.toField(ModelBinder.this.get(property)), hasValue.getEmptyValue()));
		Procedure valueWriter;
		if (property.isWritable()) {
			valueWriter = () -> ModelBinder.this.set(property, converter.toProperty(hasValue.getValue()));
		} else {
			valueWriter = NOOP;
		}
		createHasValueBinding(property, hasValue, valueReader, valueWriter);
	}

	private synchronized <FieldValueType> void createHasValueBinding(Property<ModelType, ?> property,
			HasValue<?, FieldValueType> hasValue, Procedure valueReader, Procedure valueWriter) {
		Consumer<Boolean> enabler;
		if (hasValue instanceof HasEnabled) {
			enabler = enable -> ((HasEnabled) hasValue).setEnabled(enable);
		} else {
			enabler = NOCONSUME;
		}

		Procedure enablementSwitch = () -> {
			boolean exists = ModelBinder.this.exists(property);
			hasValue.setReadOnly(!property.isWritable() || !exists);
			enabler.accept(exists);
		};

		addBinding(property, new HasValueBinding(hasValue, valueReader, valueWriter, enablementSwitch));
	}

	// ######################################################################################################################################
	// ########################################################## BINDING HANDLING ##########################################################
	// ######################################################################################################################################

	private void addBinding(Property<ModelType, ?> property, Binding binding) {
		if (!this.bindings.containsKey(property)) {
			this.bindings.put(property, new ArrayList<>());
		}
		this.bindings.get(property).add(binding);
	}

	synchronized void updateAll(UpdateType type) {
		this.bindings.forEach((property, bindings) -> bindings.forEach(binding -> binding.update(context, type)));
	}

	synchronized void update(Property<ModelType, ?> property, Context context, UpdateType type) {
		// RUN OVER ALL BINDINGS
		boundPropertyLoop: for (Property<ModelType, ?> boundProperty : this.bindings.keySet()) {
			// IF THE UPDATE'S PROPERTY IS A PARENT OF THE BINDING'S PROPERTY...
			Set<Property<?, ?>> boundHierarchy = boundProperty.getHierarchy();
			Set<Property<?, ?>> hierarchy = property.getHierarchy();
			if (boundHierarchy.containsAll(hierarchy) || hierarchy.containsAll(boundHierarchy)) {
				// GO OVER ALL OF THE UPDATE'S CONTEXTABLE PROPERTIES
				for (Property<?, ?> contextedProperty : property.getHierarchy()) {
					// CHECK IF THERE IS A KEY FOR THE CONTEXTED PROPERTY THAT IS UNEQUAL TO THE ONE IN THIS BINDER'S CONTEXT
					if (context.containsReference(contextedProperty) && this.context.containsReference(contextedProperty)
							&& !context.getReference(contextedProperty).equals(this.context.getReference(contextedProperty))) {
						// IF THERE IS ONE, THE BINDING IS NOT AFFECTED BY THE UPDATE
						continue boundPropertyLoop;
					}
				}
				this.bindings.get(boundProperty).forEach(binding -> binding.update(context, type));
			}
		}
	}

	@PreDestroy
	private synchronized void destroy() {
		Iterator<Entry<Property<ModelType, ?>, List<Binding>>> mapIter = this.bindings.entrySet().iterator();
		while (mapIter.hasNext()) {
			Iterator<Binding> bindingIter = mapIter.next().getValue().iterator();
			while (bindingIter.hasNext()) {
				bindingIter.next().unbind();
				bindingIter.remove();
			}
			mapIter.remove();
		}
	}
}