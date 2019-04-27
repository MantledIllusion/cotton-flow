package com.mantledillusion.vaadin.cotton.model;

import com.mantledillusion.data.epiphy.interfaces.ReadableProperty;
import com.vaadin.flow.component.HasValue;

/**
 * Interface for {@link Converter}s that might be used to convert between the
 * value types of a field and a property.
 * <p>
 * For example, needed when a {@link HasValue} is bound to a
 * {@link ReadableProperty} using a {@link ModelAccessor}.
 *
 * @param <FieldValueType>
 *            The value type of the field.
 * @param <PropertyValueType>
 *            The value type of the property.
 */
public interface Converter<FieldValueType, PropertyValueType> {

	/**
	 * Converts a value of the properties' value type to a value of the field's
	 * value type.
	 * 
	 * @param value
	 *            The properties' value to convert; might be null.
	 * @return The converted value for the field, might be null
	 */
	FieldValueType toField(PropertyValueType value);

	/**
	 * Converts a value of the field's value type to a value of the properties'
	 * value type.
	 * 
	 * @param value
	 *            The field's value to convert; might be null.
	 * @return The converted value for the property, might be null
	 */
	PropertyValueType toProperty(FieldValueType value);
}
