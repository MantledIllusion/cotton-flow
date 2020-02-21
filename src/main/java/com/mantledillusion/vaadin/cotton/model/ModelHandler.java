package com.mantledillusion.vaadin.cotton.model;

import java.lang.reflect.Method;

import com.mantledillusion.data.epiphy.Property;
import com.mantledillusion.data.epiphy.context.Context;
import com.mantledillusion.data.epiphy.context.function.*;
import com.mantledillusion.data.epiphy.context.reference.ReferencedValue;

/**
 * Interface for types that can hold a model and extract/change parts of it using {@link Property}s.
 * <p>
 * The default implementations to use are:<BR>
 * - {@link ModelContainer}: To hold a model and allow access to it<BR>
 * - {@link ModelAccessor}: For indexed proxying to underneath a {@link ModelContainer}
 *
 * @param <ModelType> The root type of the data model the {@link ModelHandler} is able to handle.
 */
public interface ModelHandler<ModelType> {

	// ######################################################################################################################################
	// ########################################################### MODEL CONTROL ############################################################
	// ######################################################################################################################################

	/**
	 * Returns the model instance currently contained by this {@link ModelContainer}.
	 *
	 * @return The current model instance, might be null
	 */
	ModelType getModel();

	/**
	 * Sets the current model instance of this {@link ModelContainer}.
	 *
	 * @param model The model to set; might be null.
	 */
	void setModel(ModelType model);

	/**
	 * Determines whether the given property exists in the model; or to put it differently, whether the parent
	 * properties of the property are all non-null.
	 * <p>
	 * The {@link Method} checks on the property parent's values, not on the properties' own value. If all parents are
	 * non-null but the property itself is null, the {@link Method} will still return true.
	 * <p>
	 * The result indicates whether it is safe to execute writing operations on the property.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the returned result.
	 * <p>
	 * For determination of existence, this handler's own index context is used.
	 *
	 * @param <PropertyValueType> The type of the property to check.
	 * @param property The property to check for existence; <b>not</b> allowed to be null.
	 * @return True if all of the given properties' parents are non-null, false otherwise
	 */
	<PropertyValueType> boolean exists(Property<ModelType, PropertyValueType> property);

	/**
	 * Determines whether the given property exists in the model; or to put it differently, whether the parent
	 * properties of the property are all non-null.
	 * <p>
	 * The {@link Method} checks on the property parent's values, not on the properties' own value. If all parents are
	 * non-null but the property itself is null, the {@link Method} will still return true.
	 * <p>
	 * The result indicates whether it is safe to execute writing operations on the property.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the returned result.
	 * <p>
	 * For determination of existence, the given index context is used as an extension to the handler's own index
	 * context.
	 * 
	 * @param <PropertyValueType> The type of the property to check.
	 * @param property The property to check for existence; <b>not</b> allowed to be null.
	 * @param context The context which is used for determining the correct property; might be null.
	 * @return True if all of the given properties' parents are non-null, false otherwise
	 */
	<PropertyValueType> boolean exists(Property<ModelType, PropertyValueType> property, Context context);

	// ######################################################################################################################################
	// ###################################################### PROPERTIED MODEL ACCESS #######################################################
	// ######################################################################################################################################

	/**
	 * Fetches the value from inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the returned result.
	 * <p>
	 * For determining the correct property, this handler's own index context is used.
	 * 
	 * @param <PropertyValueType> The type of the property to get.
	 * @param property The property to fetch model data for; <b>not</b> allowed to be null.
	 * @return The target data in the model the given property points to; might be null if the property is null
	 */
	<PropertyValueType> PropertyValueType get(Property<ModelType, PropertyValueType> property);

	/**
	 * Fetches the value from inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the returned result.
	 * <p>
	 * For determining the correct property, the given index context is used as an extension to the handler's own
	 * index context.
	 * 
	 * @param <PropertyValueType> The type of the property to get.
	 * @param property The property to fetch model data for; <b>not</b> allowed to be null.
	 * @param context The context which is used for determining the correct property; might be null.
	 * @return The target data in the model the given property points to; might be null if the property is null
	 */
	<PropertyValueType> PropertyValueType get(Property<ModelType, PropertyValueType> property, Context context);

	/**
	 * Sets the value inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, this handler's own index context is used.
	 * 
	 * @param <PropertyValueType> The type of the property to set.
	 * @param property The property to set inside the model; <b>not</b> allowed to be null.
	 * @param value The value to inject into the model.
	 */
	<PropertyValueType> void set(Property<ModelType, PropertyValueType> property, PropertyValueType value);

	/**
	 * Sets the value inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, the given index context is used as an extension to the handler's own
	 * index context.
	 * 
	 * @param <PropertyValueType> The type of the property to set.
	 * @param property The property to set inside the model; <b>not</b> allowed to be null.
	 * @param value The value to inject into the model; might be null.
	 * @param context The context which is used for determining the correct property; might be null.
	 */
	<PropertyValueType> void set(Property<ModelType, PropertyValueType> property, PropertyValueType value, Context context);

	/**
	 * Includes the element inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, this handler's own index context is used.
	 *
	 * @param <PropertyElementType> The type of element to include.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to include inside the model; <b>not</b> allowed to be null.
	 * @param element The element to include into the model; might be null.
	 * @return The reference of the included value, never null
	 */
	<PropertyElementType, ReferenceType> ReferenceType include(IncludableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element);

	/**
	 * Includes the element inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, the given index context is used as an extension to the handler's own
	 * index context.
	 *
	 * @param <PropertyElementType> The type of element to include.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to include inside the model; <b>not</b> allowed to be null.
	 * @param element The element to include into the model; might be null.
	 * @param context The context which is used for determining the correct property; might be null.
	 * @return The reference of the included value, never null
	 */
	<PropertyElementType, ReferenceType> ReferenceType include(IncludableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, Context context);

	/**
	 * Inserts the element inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, this handler's own index context is used.
	 *
	 * @param <PropertyElementType> The type of element to insert.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to insert inside the model; <b>not</b> allowed to be null.
	 * @param element The element to insert into the model; might be null.
	 * @param reference The reference that determines where to insert the element; <b>not</b> allowed to be null.
	 */
	<PropertyElementType, ReferenceType> void insert(InsertableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, ReferenceType reference);

	/**
	 * Inserts the element inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, the given index context is used as an extension to the handler's own
	 * index context.
	 *
	 * @param <PropertyElementType> The type of element to insert.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to insert inside the model; <b>not</b> allowed to be null.
	 * @param element The element to insert into the model; might be null.
	 * @param reference The reference that determines where to insert the element; <b>not</b> allowed to be null.
	 * @param context The context which is used for determining the correct property; might be null.
	 */
	<PropertyElementType, ReferenceType> void insert(InsertableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, ReferenceType reference, Context context);

	/**
	 * Strips the element from the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, this handler's own index context is used.
	 *
	 * @param <PropertyElementType> The type of element to strip.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to strip from the model; <b>not</b> allowed to be null.
	 * @return The reference of the stripped value, never null
	 */
	<PropertyElementType, ReferenceType> ReferencedValue<ReferenceType, PropertyElementType> strip(StripableProperty<ModelType, ?, PropertyElementType, ReferenceType> property);

	/**
	 * Strips the element from the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, the given index context is used as an extension to the handler's own
	 * index context.
	 *
	 * @param <PropertyElementType> The type of element to strip.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to strip from the model; <b>not</b> allowed to be null.
	 * @param context The context which is used for determining the correct property; might be null.
	 * @return The reference of the stripped value, never null
	 */
	<PropertyElementType, ReferenceType> ReferencedValue<ReferenceType, PropertyElementType> strip(StripableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, Context context);

	/**
	 * Drops the element from the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, this handler's own index context is used.
	 *
	 * @param <PropertyElementType> The type of element to drop.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to drop from the model; <b>not</b> allowed to be null.
	 * @param element The element to drop from the model; might be null.
	 * @return The reference of the dropped value, never null
	 */
	<PropertyElementType, ReferenceType> ReferenceType drop(DropableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element);

	/**
	 * Drops the element from the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, the given index context is used as an extension to the handler's own
	 * index context.
	 *
	 * @param <PropertyElementType> The type of element to drop.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to drop from the model; <b>not</b> allowed to be null.
	 * @param element The element to drop from the model; might be null.
	 * @param context The context which is used for determining the correct property; might be null.
	 * @return The reference of the dropped value, never null
	 */
	<PropertyElementType, ReferenceType> ReferenceType drop(DropableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, PropertyElementType element, Context context);

	/**
	 * Extracts the element from the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, this handler's own index context is used.
	 *
	 * @param <PropertyElementType> The type of element to extract.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to extract from the model; <b>not</b> allowed to be null.
	 * @param reference The reference to the element to drop from the model; might <b>not</b> be null.
	 * @return The reference of the extracted value, never null
	 */
	<PropertyElementType, ReferenceType> PropertyElementType extract(ExtractableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, ReferenceType reference);

	/**
	 * Extracts the element from the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is indexed, the used index context
	 * has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, the given index context is used as an extension to the handler's own
	 * index context.
	 *
	 * @param <PropertyElementType> The type of element to extract.
	 * @param <ReferenceType> The type that references the element.
	 * @param property The property to extract from the model; <b>not</b> allowed to be null.
	 * @param reference The reference to the element to drop from the model; might <b>not</b> be null.
	 * @param context The context which is used for determining the correct property; might be null.
	 * @return The reference of the extracted value, never null
	 */
	<PropertyElementType, ReferenceType> PropertyElementType extract(ExtractableProperty<ModelType, ?, PropertyElementType, ReferenceType> property, ReferenceType reference, Context context);
}
