package com.mantledillusion.vaadin.cotton.model;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.data.epiphy.context.Context;
import com.mantledillusion.data.epiphy.context.function.*;
import com.mantledillusion.data.epiphy.context.reference.ReferencedValue;
import org.apache.commons.lang3.ObjectUtils;

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
	public static final String SID_PROPERTYCONTEXT = "_propertyContext";

	private final List<ModelAccessor<ModelType>> children = new ArrayList<>();
	private ModelType model;

	final void register(ModelAccessor<ModelType> childAccessor) {
		this.children.add(childAccessor);
		childAccessor.updateAll(UpdateType.EXCHANGE);
	}

	final void unregister(ModelAccessor<ModelType> childAccessor) {
		this.children.remove(childAccessor);
	}

	private void updateAll(UpdateType type) {
		this.children.forEach(child -> child.updateAll(type));
	}

	private void update(Property<ModelType, ?> property, Context context, UpdateType type) {
		this.children.forEach(child -> child.update(property, context, type));
	}

	// ######################################################################################################################################
	// ########################################################### MODEL CONTROL ############################################################
	// ######################################################################################################################################

	@Override
	public ModelType getModel() {
		return model;
	}

	@Override
	public void setModel(ModelType model) {
		this.model = model;
		updateAll(UpdateType.EXCHANGE);
	}

	@Override
	public <PropertyValueType> boolean exists(Property<ModelType, PropertyValueType> property) {
		return exists(property, Context.EMPTY);
	}

	@Override
	public <PropertyValueType> boolean exists(Property<ModelType, PropertyValueType> property, Context context) {
		context = ObjectUtils.defaultIfNull(context, Context.EMPTY);
		return property.exists(this.model, context);
	}

	// ######################################################################################################################################
	// ###################################################### PROPERTIED MODEL ACCESS #######################################################
	// ######################################################################################################################################

	@Override
	public <PropertyValueType> PropertyValueType get(Property<ModelType, PropertyValueType> property) {
		return get(property, Context.EMPTY);
	}

	@Override
	public <PropertyValueType> PropertyValueType get(Property<ModelType, PropertyValueType> property, Context context) {
		context = ObjectUtils.defaultIfNull(context, Context.EMPTY);
		return property.get(this.model, context, true);
	}

	@Override
	public <PropertyValueType> void set(Property<ModelType, PropertyValueType> property,
			PropertyValueType value) {
		set(property, value, Context.EMPTY);
	}

	@Override
	public <PropertyValueType> void set(Property<ModelType, PropertyValueType> property, PropertyValueType value, Context context) {
		context = ObjectUtils.defaultIfNull(context, Context.EMPTY);
		property.set(this.model, value, context);
		update(property, context, UpdateType.EXCHANGE);
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferenceType include(IncludableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element) {
		return include(property, element, Context.EMPTY);
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferenceType include(IncludableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, Context context) {
		context = ObjectUtils.defaultIfNull(context, Context.EMPTY);
		ReferenceType reference = property.include(this.model, element, context);
		update(property, context, UpdateType.ADD);
		return reference;
	}

	@Override
	public <PropertyElementType, ReferenceType> void insert(InsertableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, ReferenceType reference) {
		this.insert(property, element, reference, Context.EMPTY);
	}

	@Override
	public <PropertyElementType, ReferenceType> void insert(InsertableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, ReferenceType reference, Context context) {
		context = ObjectUtils.defaultIfNull(context, Context.EMPTY);
		property.insert(this.model, element, reference, context);
		update(property, context, UpdateType.ADD);
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferencedValue<ReferenceType, PropertyElementType> strip(StripableProperty<ModelType, ?, PropertyElementType, ReferenceType> property) {
		return strip(property, Context.EMPTY);
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferencedValue<ReferenceType, PropertyElementType> strip(StripableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, Context context) {
		context = ObjectUtils.defaultIfNull(context, Context.EMPTY);
		ReferencedValue<ReferenceType, PropertyElementType> referencedValue = property.strip(this.model, context);
		update(property, context, UpdateType.REMOVE);
		return referencedValue;
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferenceType drop(DropableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element) {
		return drop(property, element, Context.EMPTY);
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferenceType drop(DropableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, Context context) {
		context = ObjectUtils.defaultIfNull(context, Context.EMPTY);
		ReferenceType reference = property.drop(this.model, element, context);
		update(property, context, UpdateType.REMOVE);
		return reference;
	}

	@Override
	public <PropertyElementType, ReferenceType> PropertyElementType extract(ExtractableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, ReferenceType reference) {
		return extract(property, reference, Context.EMPTY);
	}

	@Override
	public <PropertyElementType, ReferenceType> PropertyElementType extract(ExtractableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, ReferenceType reference, Context context) {
		context = ObjectUtils.defaultIfNull(context, Context.EMPTY);
		PropertyElementType element = property.extract(this.model, reference, context);
		update(property, context, UpdateType.REMOVE);
		return element;
	}
}
