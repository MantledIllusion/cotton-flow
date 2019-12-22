package com.mantledillusion.vaadin.cotton.model;

import java.util.ArrayList;
import java.util.List;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.data.epiphy.context.Context;
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
		childAccessor.updateAll(UpdateType.ADD);
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
}
