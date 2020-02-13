package com.mantledillusion.vaadin.cotton.model;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.data.epiphy.context.Context;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;
import com.vaadin.flow.component.Component;
import org.apache.commons.lang3.ObjectUtils;

import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.shared.Registration;

abstract class ModelBinder<ModelType> implements ModelHandler<ModelType> {

	private static final Procedure NOOP = () -> {};
	private static final Supplier<Binding.AccessMode> NOAUDIT = () -> null;

	private interface Procedure {

		void trigger();
	}

	enum UpdateType {
		EXCHANGE, ADD, REMOVE;
	}

	private final Context context;
	private final Map<Property<ModelType, ?>, List<Binding>> bindings = new IdentityHashMap<>();

	private Supplier<Binding.AccessMode> baseBindingAuditor = NOAUDIT;

	protected ModelBinder(Context context) {
		this.context = context;
	}

	protected Context getContext() {
		return this.context;
	}

	/**
	 * BAdds the given binding auditor to all bound bindings to restrict them.
	 * <p>
	 * The given {@link Supplier}'s result will be used to determine at which {@link Binding.AccessMode} created
	 * bindings are expected to allow access to the data of their respective bound properties.
	 * <p>
	 * When this method is never used so no binding auditor is ever specified, a default auditor will allow general
	 * {@link Binding.AccessMode#READ_WRITE} access to properties, as long as the individual {@link Binding}s do not
	 * have their own restrictions.
	 * <p>
	 * When multiple binding auditors are specified using this method, the most generous {@link Binding.AccessMode}
	 * determined by the auditors (and the possible other auditors of the {@link Binding}s) will be used.
	 *
	 * @param baseBindingAuditor The binding auditor; might <b>not</b> be null.
	 */
	public final void withBaseRestriction(Supplier<Binding.AccessMode> baseBindingAuditor) {
		this.baseBindingAuditor = Binding.AccessMode.chain(this.baseBindingAuditor, baseBindingAuditor);
		this.bindings.values().stream().flatMap(List::stream).forEach(Binding::refreshAccessMode);
	}

	// ######################################################################################################################################
	// ########################################################## CONSUMER BINDING ##########################################################
	// ######################################################################################################################################

	private class ConsumerBinding extends Binding {
		private final Procedure valueReader;
		private final Procedure valueResetter;

		private ConsumerBinding(Procedure valueReader, Procedure valueResetter) {
			super(() -> ModelBinder.this.baseBindingAuditor.get());
			this.valueReader = valueReader;
			this.valueResetter = valueResetter;

			refreshAccessMode();
		}

		@Override
		public void accessModeChanged(boolean couple) {
			if (couple) {
				this.valueReader.trigger();
			} else{
				this.valueResetter.trigger();
			}
		}

		@Override
		public void valueChanged(Context context, UpdateType type) {
			this.valueReader.trigger();
		}
	}

	/**
	 * Binds the given {@link Consumer} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType> The value type of the {@link Consumer} to bind.
	 * @param consumer The {@link Consumer} to bind; might <b>not</b> be null.
	 * @param property The {@link Property} to bind to; might <b>not</b> be null.
	 * @return The {@link Binding} to further configure the binding with, never null
	 */
	public <FieldValueType> Binding bind(Consumer<FieldValueType> consumer,
										 Property<ModelType, FieldValueType> property) {
		if (consumer == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + Consumer.class.getSimpleName());
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> consumer.accept(ModelBinder.this.get(property));
		Procedure valueResetter = () -> consumer.accept(null);

		return addBinding(property, new ConsumerBinding(valueReader, valueResetter));
	}

	/**
	 * Binds the given {@link Consumer} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType> The value type of the {@link Consumer} to bind.
	 * @param <PropertyValueType> The value type of the property to bind with.
	 * @param consumer The {@link Consumer} to bind; might <b>not</b> be null.
	 * @param converter The {@link Converter} to use for conversion between the field's and the properties' value
	 *                  types; might <b>not</b> be null.
	 * @param property The {@link Property} to bind to; might <b>not</b> be null.
	 * @return The {@link Binding} to further configure the binding with, never null
	 */
	public <FieldValueType, PropertyValueType> Binding bind(Consumer<FieldValueType> consumer,
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
		Procedure valueResetter = () -> consumer.accept(null);

		return addBinding(property, new ConsumerBinding(valueReader, valueResetter));
	}

	// ######################################################################################################################################
	// ########################################################## HASVALUE BINDING ##########################################################
	// ######################################################################################################################################

	private class HasValueBinding extends Binding implements ValueChangeListener {

		private final HasValue<?, ?> hasValue;
		private final Property<ModelType, ?> property;
		private final Procedure valueReader;
		private final Procedure valueWriter;
		private final Procedure valueResetter;

		private Registration registration;
		private boolean synchronizing = false;

		public HasValueBinding(HasValue<?, ?> hasValue, Property<ModelType, ?> property,
							   Procedure valueReader, Procedure valueWriter, Procedure valueResetter) {
			super(() -> ModelBinder.this.baseBindingAuditor.get());
			this.hasValue = hasValue;
			this.property = property;
			this.valueReader = valueReader;
			this.valueWriter = valueWriter;
			this.valueResetter = valueResetter;

			refreshAccessMode();
		}

		@Override
		public synchronized void accessModeChanged(boolean couple) {
			if (couple && this.registration == null) {
				this.registration = this.hasValue.addValueChangeListener(this);
				this.valueReader.trigger();
			} else if (!couple && this.registration != null) {
				this.registration.remove();
				this.registration = null;
				this.valueResetter.trigger();
			}

			if (this.hasValue instanceof Component) {
				((Component) this.hasValue).setVisible(getAccessMode() != AccessMode.HIDDEN);
			}
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
		public synchronized void valueChanged(Context context, UpdateType type) {
			if (!this.synchronizing) {
				this.synchronizing = true;
				boolean exists = ModelBinder.this.exists(property);
				this.hasValue.setReadOnly(this.property.isWritable() && exists &&
						getAccessMode() != AccessMode.READ_WRITE);
				if (this.hasValue instanceof HasEnabled) {
					((HasEnabled) this.hasValue).setEnabled(exists);
				}
				if (this.registration != null) {
					this.valueReader.trigger();
				}
				this.synchronizing = false;
			}
		}
	}

	/**
	 * Binds the given {@link HasValue} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType> The value type of the {@link HasValue} to bind.
	 * @param hasValue The {@link HasValue} to bind; might <b>not</b> be null.
	 * @param property The {@link Property} to bind to; might <b>not</b> be null.
	 * @return The {@link Binding} to further configure the binding with, never null
	 */
	public <FieldValueType> Binding bind(HasValue<?, FieldValueType> hasValue,
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
		Procedure valueResetter = () -> hasValue.setValue(hasValue.getEmptyValue());

		return addBinding(property, new HasValueBinding(hasValue, property, valueReader, valueWriter, valueResetter));
	}

	/**
	 * Binds the given {@link HasValue} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType> The value type of the {@link HasValue} to bind.
	 * @param <PropertyValueType> The value type of the property to bind with.
	 * @param hasValue The {@link HasValue} to bind; might <b>not</b> be null.
	 * @param converter The {@link Converter} to use for conversion between the field's and the properties' value
	 *                  types; might <b>not</b> be null.
	 * @param property The {@link Property} to bind to; might <b>not</b> be null.
	 * @return The {@link Binding} to further configure the binding with, never null
	 */
	public <FieldValueType, PropertyValueType> Binding bind(HasValue<?, FieldValueType> hasValue,
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
		Procedure valueResetter = () -> hasValue.setValue(hasValue.getEmptyValue());

		return addBinding(property, new HasValueBinding(hasValue, property, valueReader, valueWriter, valueResetter));
	}

	// ######################################################################################################################################
	// ########################################################## BINDING HANDLING ##########################################################
	// ######################################################################################################################################

	private Binding addBinding(Property<ModelType, ?> property, Binding binding) {
		if (!this.bindings.containsKey(property)) {
			this.bindings.put(property, new ArrayList<>());
		}
		this.bindings.get(property).add(binding);
		return binding;
	}

	synchronized void updateAll(UpdateType type) {
		this.bindings.forEach((property, bindings) -> bindings.forEach(binding -> binding.valueChanged(context, type)));
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
				this.bindings.get(boundProperty).forEach(binding -> binding.valueChanged(context, type));
			}
		}
	}

	@PreDestroy
	private synchronized void destroy() {
		Iterator<Entry<Property<ModelType, ?>, List<Binding>>> mapIter = this.bindings.entrySet().iterator();
		while (mapIter.hasNext()) {
			Iterator<Binding> bindingIter = mapIter.next().getValue().iterator();
			while (bindingIter.hasNext()) {
				bindingIter.next().accessModeChanged(false);
				bindingIter.remove();
			}
			mapIter.remove();
		}
	}
}