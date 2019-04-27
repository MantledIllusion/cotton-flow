package com.mantledillusion.vaadin.cotton.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.mantledillusion.data.epiphy.context.ContextedValue;
import com.mantledillusion.data.epiphy.interfaces.ReadableProperty;
import com.mantledillusion.data.epiphy.interfaces.WriteableProperty;
import com.mantledillusion.data.epiphy.interfaces.function.ContextableProperty;
import com.mantledillusion.data.epiphy.interfaces.function.EnumerableProperty;
import com.mantledillusion.data.epiphy.interfaces.function.IdentifyableProperty;
import com.mantledillusion.vaadin.cotton.model.ModelBinder.UpdateType;

/**
 * {@link ModelHandler} implementation that contains the model instance.
 *
 * @param <ModelType>
 *            The root type of the data model the {@link ModelContainer} is able
 *            to contain.
 */
public final class ModelContainer<ModelType> implements ModelHandler<ModelType> {

	public static final String SID_CONTAINER = "_modelContainer";

	private final List<ModelAccessor<ModelType>> children = new ArrayList<>();
	private ModelType model;

	final void register(ModelAccessor<ModelType> childAccessor) {
		this.children.add(childAccessor);
		childAccessor.update(UpdateType.ADD);
	}

	final void unregister(ModelAccessor<ModelType> childAccessor) {
		this.children.remove(childAccessor);
	}

	private void update(UpdateType type) {
		this.children.forEach(child -> child.update(type));
	}

	private void update(IdentifyableProperty<ModelType> property, PropertyContext context, UpdateType type) {
		this.children.forEach(child -> child.update(property, context, type));
	}

	// ######################################################################################################################################
	// ########################################################### MODEL CONTROL ############################################################
	// ######################################################################################################################################

	/**
	 * Returns the model instance currently contained by this
	 * {@link ModelContainer}.
	 * 
	 * @return The current model instance, might be null
	 */
	public ModelType getModel() {
		return model;
	}

	/**
	 * Sets the current model instance of this {@link ModelContainer}.
	 * 
	 * @param model
	 *            The model to set; might be null.
	 */
	public void setModel(ModelType model) {
		this.model = model;
		update(UpdateType.EXCHANGE);
	}

	@Override
	public <PropertyValueType> boolean exists(ReadableProperty<ModelType, PropertyValueType> property) {
		return exists(property, PropertyContext.EMPTY);
	}

	@Override
	public <PropertyValueType> boolean exists(ReadableProperty<ModelType, PropertyValueType> property,
			PropertyContext context) {
		context = ObjectUtils.defaultIfNull(context, PropertyContext.EMPTY);
		return property.exists(this.model, context);
	}

	// ######################################################################################################################################
	// ###################################################### PROPERTIED MODEL ACCESS #######################################################
	// ######################################################################################################################################

	@Override
	public <PropertyValueType> PropertyValueType get(ReadableProperty<ModelType, PropertyValueType> property) {
		return get(property, PropertyContext.EMPTY);
	}

	@Override
	public <PropertyValueType> PropertyValueType get(ReadableProperty<ModelType, PropertyValueType> property,
			PropertyContext context) {
		context = ObjectUtils.defaultIfNull(context, PropertyContext.EMPTY);
		return property.get(this.model, context, true);
	}

	@Override
	public <PropertyValueType> void set(WriteableProperty<ModelType, PropertyValueType> property,
			PropertyValueType value) {
		set(property, value, PropertyContext.EMPTY);
	}

	@Override
	public <PropertyValueType> void set(WriteableProperty<ModelType, PropertyValueType> property,
			PropertyValueType value, PropertyContext context) {
		context = ObjectUtils.defaultIfNull(context, PropertyContext.EMPTY);
		property.set(this.model, value, context);
		update(property, context, UpdateType.EXCHANGE);
	}

	@Override
	public <PropertyType, ReferenceType> ReferenceType append(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element) {
		return append(property, element, PropertyContext.EMPTY);
	}

	@Override
	public <PropertyType, ReferenceType> ReferenceType append(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element,
			PropertyContext context) {
		context = ObjectUtils.defaultIfNull(context, PropertyContext.EMPTY);
		ReferenceType reference = property.append(this.model, element, context);
		context = context.union(property.referenceOf(reference));
		update(property, context, UpdateType.ADD);
		return reference;
	}

	@Override
	public <PropertyType> void addAt(ContextableProperty<ModelType, PropertyType, ?> property, PropertyType value,
			PropertyContext context) {
		context = ObjectUtils.defaultIfNull(context, PropertyContext.EMPTY);
		property.addAt(this.model, value, context);
		update(property, context, UpdateType.ADD);
	}

	@Override
	public <PropertyType, ReferenceType> ContextedValue<ReferenceType, PropertyType> strip(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property) {
		return strip(property, PropertyContext.EMPTY);
	}

	@Override
	public <PropertyType, ReferenceType> ContextedValue<ReferenceType, PropertyType> strip(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property, PropertyContext context) {
		context = ObjectUtils.defaultIfNull(context, PropertyContext.EMPTY);
		ContextedValue<ReferenceType, PropertyType> value = property.strip(this.model, context);
		if (value != null) {
			context = context.union(property.referenceOf(value.getPropertyKey()));
			update(property, context, UpdateType.REMOVE);
			return value;
		} else {
			return null;
		}
	}

	@Override
	public <PropertyType> PropertyType removeAt(ContextableProperty<ModelType, PropertyType, ?> property,
			PropertyContext context) {
		context = ObjectUtils.defaultIfNull(context, PropertyContext.EMPTY);
		PropertyType element = property.removeAt(this.model, context);
		update(property, context, UpdateType.REMOVE);
		return element;
	}

	@Override
	public <PropertyType, ReferenceType> ReferenceType remove(
			ContextableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element) {
		return remove(property, element, PropertyContext.EMPTY);
	}

	@Override
	public <PropertyType, ReferenceType> ReferenceType remove(
			ContextableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element,
			PropertyContext context) {
		context = ObjectUtils.defaultIfNull(context, PropertyContext.EMPTY);
		ReferenceType reference = property.remove(this.model, element, context);
		if (reference != null) {
			context = context.union(property.referenceOf(reference));
			update(property, context, UpdateType.REMOVE);
			return reference;
		} else {
			return null;
		}
	}
}
