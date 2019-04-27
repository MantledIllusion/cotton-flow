package com.mantledillusion.vaadin.cotton.model;

import java.lang.reflect.Method;

import com.mantledillusion.data.epiphy.context.ContextedValue;
import com.mantledillusion.data.epiphy.interfaces.ReadableProperty;
import com.mantledillusion.data.epiphy.interfaces.WriteableProperty;
import com.mantledillusion.data.epiphy.interfaces.function.ContextableProperty;
import com.mantledillusion.data.epiphy.interfaces.function.EnumerableProperty;

/**
 * Interface for types that can hold a model and extract/change parts of it
 * using {@link ReadableProperty}s.
 * <p>
 * The default implementations to use are:<BR>
 * - {@link ModelContainer}: To hold a model and allow access to it<BR>
 * - {@link ModelAccessor}: For indexed proxying to underneath a
 * {@link ModelContainer}
 *
 * @param <ModelType>
 *            The root type of the data model the {@link ModelHandler} is able
 *            to handle.
 */
public interface ModelHandler<ModelType> {

	// ######################################################################################################################################
	// ########################################################### MODEL CONTROL ############################################################
	// ######################################################################################################################################

	/**
	 * Determines whether the given property exists in the model; or to put it
	 * differently, whether the parent properties of the property are all non-null.
	 * <p>
	 * The {@link Method} checks on the property parent's values, not on the
	 * properties' own value. If all parents are non-null but the property itself is
	 * null, the {@link Method} will still return true.
	 * <p>
	 * The result indicates whether it is safe to execute writing operations on the
	 * property.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the returned result.
	 * <p>
	 * For determination of existence, this handler's own index context is used.
	 * 
	 * @param <PropertyValueType>
	 *            The type of the property to check.
	 * @param property
	 *            The property to check for existence; <b>not</b> allowed to be
	 *            null.
	 * @return True if all of the given properties' parents are non-null, false
	 *         otherwise
	 */
	<PropertyValueType> boolean exists(ReadableProperty<ModelType, PropertyValueType> property);

	/**
	 * Determines whether the given property exists in the model; or to put it
	 * differently, whether the parent properties of the property are all non-null.
	 * <p>
	 * The {@link Method} checks on the property parent's values, not on the
	 * properties' own value. If all parents are non-null but the property itself is
	 * null, the {@link Method} will still return true.
	 * <p>
	 * The result indicates whether it is safe to execute writing operations on the
	 * property.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the returned result.
	 * <p>
	 * For determination of existence, the given index context is used as an
	 * extension to the handler's own index context.
	 * 
	 * @param <PropertyValueType>
	 *            The type of the property to check.
	 * @param property
	 *            The property to check for existence; <b>not</b> allowed to be
	 *            null.
	 * @param context
	 *            The context which is used for determining the correct property;
	 *            might be null.
	 * @return True if all of the given properties' parents are non-null, false
	 *         otherwise
	 */
	<PropertyValueType> boolean exists(ReadableProperty<ModelType, PropertyValueType> property,
			PropertyContext context);

	// ######################################################################################################################################
	// ###################################################### PROPERTIED MODEL ACCESS #######################################################
	// ######################################################################################################################################

	/**
	 * Fetches the value from inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the returned result.
	 * <p>
	 * For determining the correct property, this handler's own index context is
	 * used.
	 * 
	 * @param <PropertyValueType>
	 *            The type of the property to get.
	 * @param property
	 *            The property to fetch model data for; <b>not</b> allowed to be
	 *            null.
	 * @return The target data in the model the given property points to; might be
	 *         null if the property is null
	 */
	<PropertyValueType> PropertyValueType get(ReadableProperty<ModelType, PropertyValueType> property);

	/**
	 * Fetches the value from inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the returned result.
	 * <p>
	 * For determining the correct property, the given index context is used as an
	 * extension to the handler's own index context.
	 * 
	 * @param <PropertyValueType>
	 *            The type of the property to get.
	 * @param property
	 *            The property to fetch model data for; <b>not</b> allowed to be
	 *            null.
	 * @param context
	 *            The context which is used for determining the correct property;
	 *            might be null.
	 * @return The target data in the model the given property points to; might be
	 *         null if the property is null
	 */
	<PropertyValueType> PropertyValueType get(ReadableProperty<ModelType, PropertyValueType> property,
			PropertyContext context);

	/**
	 * Sets the value inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, this handler's own index context is
	 * used.
	 * 
	 * @param <PropertyValueType>
	 *            The type of the property to set.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param value
	 *            The value to inject into the model.
	 */
	<PropertyValueType> void set(WriteableProperty<ModelType, PropertyValueType> property, PropertyValueType value);

	/**
	 * Sets the value inside the model data the given property points to.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the execution's result.
	 * <p>
	 * For determining the correct property, the given index context is used as an
	 * extension to the handler's own index context.
	 * 
	 * @param <PropertyValueType>
	 *            The type of the property to set.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param value
	 *            The value to inject into the model; might be null.
	 * @param context
	 *            The context which is used for determining the correct property;
	 *            might be null.
	 */
	<PropertyValueType> void set(WriteableProperty<ModelType, PropertyValueType> property, PropertyValueType value,
			PropertyContext context);

	/**
	 * Adds an item to a list inside the model data the given property points to.
	 * <p>
	 * For determining the position to add, the position after the last element
	 * available is taken.
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
	 * @param <ReferenceType>
	 *            The reference type of the property to append.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param element
	 *            The element to insert into the list; might be null.
	 * @return The reference at which the element was added, never null
	 */
	<PropertyType, ReferenceType> ReferenceType append(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element);

	/**
	 * Adds an item to a list inside the model data the given property points to.
	 * <p>
	 * For determining the position to add, the position after the last element
	 * available is taken.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the execution's result. The
	 * index of the given property also determines what will be the index of the
	 * given value in the list after adding.
	 * <p>
	 * For determining the correct property, the given index context is used as an
	 * extension to the handler's own index context.
	 * 
	 * @param <PropertyType>
	 *            The type of the property to add.
	 * @param <ReferenceType>
	 *            The reference type of the property to append.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param element
	 *            The element to insert into the list; might be null.
	 * @param context
	 *            The context which is used for determining the correct property;
	 *            might be null.
	 * @return The reference at which the element was added, never null
	 */
	<PropertyType, ReferenceType> ReferenceType append(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element,
			PropertyContext context);

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
	 * For determining the correct property, the given index context is used as an
	 * extension to the handler's own index context.
	 * 
	 * @param <PropertyType>
	 *            The type of the property to add.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param element
	 *            The element to insert into the list; might be null.
	 * @param context
	 *            The context which is used for determining the correct property;
	 *            might be null.
	 */
	<PropertyType> void addAt(ContextableProperty<ModelType, PropertyType, ?> property, PropertyType element,
			PropertyContext context);

	/**
	 * Removes an item from a list inside the model data the given property points
	 * to.
	 * <p>
	 * For determining the element to remove, the last element available is taken.
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
	 *            The type of the property to strip.
	 * @param <ReferenceType>
	 *            The reference type of the property to strip.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @return A {@link ContextedValue} containing the index and the element that
	 *         has been removed from the list; might be null if there were no items
	 *         left to remove
	 */
	<PropertyType, ReferenceType> ContextedValue<ReferenceType, PropertyType> strip(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property);

	/**
	 * Removes an item from a list inside the model data the given property points
	 * to.
	 * <p>
	 * For determining the element to remove, the last element available is taken.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the execution's result. The
	 * index of the given property also determines what the index of the item will
	 * be that is removed from the list.
	 * <p>
	 * For determining the correct property, the given index context is used as an
	 * extension to the handler's own index context.
	 * 
	 * @param <PropertyType>
	 *            The type of the property to strip.
	 * @param <ReferenceType>
	 *            The reference type of the property to strip.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param context
	 *            The context which is used for determining the correct property;
	 *            might be null.
	 * @return A {@link ContextedValue} containing the index and the element that
	 *         has been removed from the list; might be null if there were no items
	 *         left to remove
	 */
	<PropertyType, ReferenceType> ContextedValue<ReferenceType, PropertyType> strip(
			EnumerableProperty<ModelType, PropertyType, ReferenceType> property, PropertyContext context);

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
	 * For determining the correct property, the given index context is used as an
	 * extension to the handler's own index context.
	 * 
	 * @param <PropertyType>
	 *            The type of the property to remove.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param context
	 *            The context which is used for determining the correct property;
	 *            might be null.
	 * @return The element that has been removed from the list; might be null if the
	 *         property is null
	 */
	<PropertyType> PropertyType removeAt(ContextableProperty<ModelType, PropertyType, ?> property,
			PropertyContext context);

	/**
	 * Removes an item from a list inside the model data the given property points
	 * to.
	 * <p>
	 * For determining the element to remove, the element's equality is used.
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
	 * @param <ReferenceType>
	 *            The reference type of the property to remove.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param element
	 *            The element to check the item's equality against; might be null.
	 * @return The reference of the element that has been removed from the list;
	 *         might be null if the property is null
	 */
	<PropertyType, ReferenceType> ReferenceType remove(
			ContextableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element);

	/**
	 * Removes an item from a list inside the model data the given property points
	 * to.
	 * <p>
	 * For determining the element to remove, the element's equality is used.
	 * <p>
	 * Note that if the path from the property model's root to the given property is
	 * indexed, the used index context has an impact on the execution's result. The
	 * index of the given property also determines what the index of the item will
	 * be that is removed from the list.
	 * <p>
	 * For determining the correct property, the given index context is used as an
	 * extension to the handler's own index context.
	 * 
	 * @param <PropertyType>
	 *            The type of the property to remove.
	 * @param <ReferenceType>
	 *            The reference type of the property to remove.
	 * @param property
	 *            The property to set inside the model; <b>not</b> allowed to be
	 *            null.
	 * @param element
	 *            The element to check the item's equality against; might be null.
	 * @param context
	 *            The context which is used for determining the correct property;
	 *            might be null.
	 * @return The reference of the element that has been removed from the list;
	 *         might be null if the property is null
	 */
	<PropertyType, ReferenceType> ReferenceType remove(
			ContextableProperty<ModelType, PropertyType, ReferenceType> property, PropertyType element,
			PropertyContext context);
}
