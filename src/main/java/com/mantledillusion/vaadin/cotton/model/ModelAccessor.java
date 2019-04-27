package com.mantledillusion.vaadin.cotton.model;

import org.apache.commons.lang3.ObjectUtils;

import com.mantledillusion.data.epiphy.context.ContextedValue;
import com.mantledillusion.data.epiphy.interfaces.ReadableProperty;
import com.mantledillusion.data.epiphy.interfaces.WriteableProperty;
import com.mantledillusion.data.epiphy.interfaces.function.ContextableProperty;
import com.mantledillusion.data.epiphy.interfaces.function.EnumerableProperty;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Optional;
import com.mantledillusion.injection.hura.annotation.Process;

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

	private ModelAccessor(@Inject(ModelContainer.SID_CONTAINER) ModelContainer<ModelType> parent,
			@Inject(PropertyContext.SID_PROPERTYCONTEXT) @Optional PropertyContext context) {
		super(ObjectUtils.defaultIfNull(context, PropertyContext.EMPTY));
		this.parent = parent;
		this.parent.register(this);
	}

	@Process(Phase.DESTROY)
	private void destroy() {
		this.parent.unregister(this);
	}

	// ######################################################################################################################################
	// ########################################################### MODEL CONTROL ############################################################
	// ######################################################################################################################################

	@Override
	public <PropertyValueType> boolean exists(ReadableProperty<ModelType, PropertyValueType> property) {
		return this.parent.exists(property, getContext());
	}

	@Override
	public <PropertyValueType> boolean exists(ReadableProperty<ModelType, PropertyValueType> property,
			PropertyContext context) {
		return this.parent.exists(property, getContext().union(context));
	}

	// ######################################################################################################################################
	// ###################################################### PROPERTIED MODEL ACCESS #######################################################
	// ######################################################################################################################################

	@Override
	public <PropertyValueType> PropertyValueType get(ReadableProperty<ModelType, PropertyValueType> property) {
		return this.parent.get(property, getContext());
	}

	@Override
	public <PropertyValueType> PropertyValueType get(ReadableProperty<ModelType, PropertyValueType> property,
			PropertyContext context) {
		return this.parent.get(property, getContext().union(context));
	}

	@Override
	public <PropertyValueType> void set(WriteableProperty<ModelType, PropertyValueType> property,
			PropertyValueType value) {
		this.parent.set(property, value, getContext());
	}

	@Override
	public <PropertyValueType> void set(WriteableProperty<ModelType, PropertyValueType> property,
			PropertyValueType value, PropertyContext context) {
		this.parent.set(property, value, getContext().union(context));
	}

	@Override
	public <PropertyType, ReferenceType> ReferenceType append(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element) {
		return this.parent.append(property, element, getContext());
	}

	@Override
	public <PropertyType, ReferenceType> ReferenceType append(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element,
			PropertyContext context) {
		return this.parent.append(property, element, getContext().union(context));
	}

	/**
	 * Adds an item to a list inside the model data the given property points to.
	 * <p>
	 * For determining the position to add, the given context is used.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the execution's result. The
	 * index of the given property also determines what will be the index of the
	 * given value in the list after adding.
	 * <p>
	 * For determining the correct property, this handler's own index context is
	 * used.
	 * 
	 * @param <PropertyType>
	 *            The type of the property to add.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param element
	 *            The element to insert into the list; might be null.
	 */
	public <PropertyType> void addAt(ContextableProperty<ModelType, PropertyType, ?> property, PropertyType element) {
		this.parent.addAt(property, element, getContext());
	}

	@Override
	public <PropertyType> void addAt(ContextableProperty<ModelType, PropertyType, ?> property, PropertyType element,
			PropertyContext context) {
		this.parent.addAt(property, element, getContext().union(context));
	}

	@Override
	public <PropertyType, ReferenceType> ContextedValue<ReferenceType, PropertyType> strip(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property) {
		return this.parent.strip(property, getContext());
	}

	@Override
	public <PropertyType, ReferenceType> ContextedValue<ReferenceType, PropertyType> strip(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property, PropertyContext context) {
		return this.parent.strip(property, getContext().union(context));
	}

	/**
	 * Removes an item from a list inside the model data the given property points
	 * to.
	 * <p>
	 * For determining the element to remove, the element's index is used.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the execution's result. The
	 * index of the given property also determines what the index of the item will
	 * be that is removed from the list.
	 * <p>
	 * For determining the correct property, this handler's own index context is
	 * used.
	 * 
	 * @param <PropertyType>
	 *            The type of the property to remove.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @return The element that has been removed from the list; might be null if the
	 *         property is null
	 */
	public <PropertyType> PropertyType removeAt(ContextableProperty<ModelType, PropertyType, ?> property) {
		return this.parent.removeAt(property, getContext());
	}

	@Override
	public <PropertyType> PropertyType removeAt(ContextableProperty<ModelType, PropertyType, ?> property,
			PropertyContext context) {
		return this.parent.removeAt(property, getContext().union(context));
	}

	@Override
	public <PropertyType, ReferenceType> ReferenceType remove(
			ContextableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element) {
		return this.parent.remove(property, element, getContext());
	}

	@Override
	public <PropertyType, ReferenceType> ReferenceType remove(
			ContextableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element,
			PropertyContext context) {
		return this.parent.remove(property, element, getContext().union(context));
	}
}
