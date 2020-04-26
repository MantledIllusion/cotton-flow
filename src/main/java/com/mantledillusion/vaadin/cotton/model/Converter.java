package com.mantledillusion.vaadin.cotton.model;

import com.mantledillusion.vaadin.cotton.exception.http900.Http903NotImplementedException;
import com.vaadin.flow.component.HasValue;

/**
 * Interface for {@link Converter}s that might be used to convert between the value types of a field and a property.
 * <p>
 * For example, needed when a {@link HasValue} is bound to a {@link com.mantledillusion.data.epiphy.Property} using
 * a {@link ModelAccessor}.
 *
 * @param <FieldValueType>
 *            The value type of the field.
 * @param <PropertyValueType>
 *            The value type of the property.
 */
@FunctionalInterface
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
	 * @throws Http903NotImplementedException When called without being overridden
	 */
	default PropertyValueType toProperty(FieldValueType value) {
		throw new Http903NotImplementedException("Converting to the property value type is not implemented");
	}
}
