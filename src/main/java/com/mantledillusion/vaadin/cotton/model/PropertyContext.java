package com.mantledillusion.vaadin.cotton.model;

import java.util.IdentityHashMap;
import java.util.Map;

import com.mantledillusion.data.epiphy.context.Context;
import com.mantledillusion.data.epiphy.context.PropertyReference;
import com.mantledillusion.data.epiphy.interfaces.function.ContextableProperty;

/**
 * Default implementation of {@link Context}.
 * <p>
 * {@link PropertyContext}s can be created using the
 * {@link #of(PropertyReference...)} method.
 */
public class PropertyContext implements Context {

	public static final String SID_PROPERTYCONTEXT = "_propertyContext";

	/**
	 * An empty {@link PropertyContext} instance without any indices.
	 */
	public static final PropertyContext EMPTY = new PropertyContext();

	private final Map<ContextableProperty<?, ?, ?>, PropertyReference<?, ?>> references;

	private PropertyContext() {
		this(new IdentityHashMap<>());
	}

	private PropertyContext(Map<ContextableProperty<?, ?, ?>, PropertyReference<?, ?>> keys) {
		this.references = keys;
	}

	@Override
	public <ReferenceType> boolean containsKey(ContextableProperty<?, ?, ReferenceType> property) {
		return this.references.containsKey(property);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <ReferenceType> ReferenceType getKey(ContextableProperty<?, ?, ReferenceType> property) {
		return this.references.containsKey(property) ? (ReferenceType) this.references.get(property).getReference()
				: null;
	}

	/**
	 * Creates an extension of this {@link PropertyContext} with the references of
	 * this context extended by the given ones.
	 * <P>
	 * Speaking in set theory, this operation is an union.
	 * 
	 * @param references
	 *            The references to add to the set of this {@link PropertyContext}s'
	 *            references for creating the extended context; might be null or
	 *            contain null values.
	 * @return The extended {@link PropertyContext}; never null
	 */
	public PropertyContext union(PropertyReference<?, ?>... references) {
		Map<ContextableProperty<?, ?, ?>, PropertyReference<?, ?>> newReferences = new IdentityHashMap<>(
				this.references);
		if (references != null) {
			for (PropertyReference<?, ?> reference : references) {
				if (reference != null) {
					newReferences.put(reference.getProperty(), reference);
				}
			}
		}
		return new PropertyContext(newReferences);
	}

	/**
	 * Creates an extension of this {@link PropertyContext} with the references of
	 * this context extended by the ones of the given context.
	 * <P>
	 * Speaking in set theory, this operation is an union.
	 * 
	 * @param other
	 *            The context's references to add to the set of this
	 *            {@link PropertyContext}s' references for creating the extended
	 *            context; might be null.
	 * @return The extended {@link PropertyContext}; never null
	 */
	public PropertyContext union(PropertyContext other) {
		Map<ContextableProperty<?, ?, ?>, PropertyReference<?, ?>> keys = new IdentityHashMap<>(this.references);
		if (other != null) {
			keys.putAll(other.references);
		}
		return new PropertyContext(keys);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((references == null) ? 0 : references.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyContext other = (PropertyContext) obj;
		if (references == null) {
			if (other.references != null)
				return false;
		} else if (!references.equals(other.references))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PropertyContext [propertyKeys=" + references + "]";
	}

	/**
	 * Creates a new {@link PropertyContext} using the given references.
	 * 
	 * @param references
	 *            The property references to create a new context from; might be
	 *            null or contain null values.
	 * @return A new {@link PropertyContext} of the given references; never null
	 */
	@SafeVarargs
	public static PropertyContext of(PropertyReference<?, ?>... references) {
		if (references == null || references.length == 0) {
			return EMPTY;
		} else {
			PropertyContext context = new PropertyContext();
			for (PropertyReference<?, ?> reference : references) {
				if (reference != null) {
					context.references.put(reference.getProperty(), reference);
				}
			}
			return context;
		}
	}
}