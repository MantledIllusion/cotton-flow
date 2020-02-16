package com.mantledillusion.vaadin.cotton.model;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.data.epiphy.context.Context;
import com.mantledillusion.data.epiphy.context.function.*;
import com.mantledillusion.data.epiphy.context.reference.ReferencedValue;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;
import org.apache.commons.lang3.ObjectUtils;

/**
 * {@link ModelHandler} implementation that is a child to a
 * {@link ModelContainer} it allows access to.
 *
 * @param <ModelType>
 *            The root type of the data model the {@link ModelAccessor} is able
 *            to grant access to.
 */
public final class ModelAccessor<ModelType> extends ModelBinder<ModelType> {

	private final ModelContainer<ModelType> parent;

	private ModelAccessor(@Inject @Qualifier(ModelContainer.SID_CONTAINER) ModelContainer<ModelType> parent,
			@Inject @Qualifier(ModelContainer.SID_PROPERTYCONTEXT) @Optional Context context) {
		super(ObjectUtils.defaultIfNull(context, Context.EMPTY));
		this.parent = parent;
		this.parent.register(this);
	}

	@PreDestroy
	private void destroy() {
		this.parent.unregister(this);
	}

	// ######################################################################################################################################
	// ########################################################### MODEL CONTROL ############################################################
	// ######################################################################################################################################


	@Override
	public ModelType getModel() {
		return this.parent.getModel();
	}

	@Override
	public void setModel(ModelType model) {
		this.parent.setModel(model);
	}

	@Override
	public <PropertyValueType> boolean exists(Property<ModelType, PropertyValueType> property) {
		return this.parent.exists(property, getContext());
	}

	@Override
	public <PropertyValueType> boolean exists(Property<ModelType, PropertyValueType> property, Context context) {
		return this.parent.exists(property, getContext().union(context));
	}

	// ######################################################################################################################################
	// ###################################################### PROPERTIED MODEL ACCESS #######################################################
	// ######################################################################################################################################

	@Override
	public <PropertyValueType> PropertyValueType get(Property<ModelType, PropertyValueType> property) {
		return this.parent.get(property, getContext());
	}

	@Override
	public <PropertyValueType> PropertyValueType get(Property<ModelType, PropertyValueType> property, Context context) {
		return this.parent.get(property, getContext().union(context));
	}

	@Override
	public <PropertyValueType> void set(Property<ModelType, PropertyValueType> property,
			PropertyValueType value) {
		this.parent.set(property, value, getContext());
	}

	@Override
	public <PropertyValueType> void set(Property<ModelType, PropertyValueType> property, PropertyValueType value, Context context) {
		this.parent.set(property, value, getContext().union(context));
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferenceType include(IncludableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element) {
		return this.parent.include(property, element, getContext());
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferenceType include(IncludableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, Context context) {
		return this.parent.include(property, element, getContext().union(context));
	}

	@Override
	public <PropertyElementType, ReferenceType> void insert(InsertableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, ReferenceType reference) {
		this.parent.insert(property, element, reference, getContext());
	}

	@Override
	public <PropertyElementType, ReferenceType> void insert(InsertableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, ReferenceType reference, Context context) {
		this.parent.insert(property, element, reference, getContext().union(context));
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferencedValue<ReferenceType, PropertyElementType> strip(StripableProperty<ModelType, ?, PropertyElementType, ReferenceType> property) {
		return this.parent.strip(property, getContext());
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferencedValue<ReferenceType, PropertyElementType> strip(StripableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, Context context) {
		return this.parent.strip(property, getContext().union(context));
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferenceType drop(DropableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element) {
		return this.parent.drop(property, element, getContext());
	}

	@Override
	public <PropertyElementType, ReferenceType> ReferenceType drop(DropableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, Context context) {
		return this.parent.drop(property, element, getContext().union(context));
	}

	@Override
	public <PropertyElementType, ReferenceType> PropertyElementType extract(ExtractableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, ReferenceType reference) {
		return this.parent.extract(property, reference, getContext());
	}

	@Override
	public <PropertyElementType, ReferenceType> PropertyElementType extract(ExtractableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, ReferenceType reference, Context context) {
		return this.parent.extract(property, reference, getContext().union(context));
	}
}
