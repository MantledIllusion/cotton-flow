package com.mantledillusion.vaadin.cotton.component;

import com.vaadin.flow.component.Component;

import java.util.List;

/**
 * Interface for {@link Component} builders.
 *
 * @param <C>
 *            The {@link Component} type this builder builds. Not an extension
 *            of {@link Component} on purpose, since Vaadin handles shared
 *            {@link Component} behavior using interfaces that are not
 *            necessarily bound to {@link Component}s.
 * @param <B>
 *            The final implementation type of this {@link EntityBuilder}.
 *            Necessary to allow builder methods of non-final implementations to
 *            return the builder instance in the correct type.
 */
public interface EntityBuilder<C, B extends EntityBuilder<C, B>> {

	/**
	 * Returns whether there is a value of the given type contained in the currently running configuration context.
	 *
	 * @param <V> The type of the value.
	 * @param valueType The class of the value's type; might be null.
	 * @return True if the configuration context contains a value of the type, false otherwise
	 */
	<V> boolean contains(Class<V> valueType);

	/**
	 * Retrieves the set value of the given type.
	 *
	 * @param <V> The type of the value.
	 * @param valueType The class of the value's type; might be null.
	 * @return The set value, might be null if explicitly set to null for the type
	 */
	<V> V get(Class<V> valueType);

	/**
	 * Sets the value for the given value type.
	 *
	 * @param <V> The retrieval type of the value.
	 * @param <VC> The concrete type of the value.
	 * @param valueType The value type to set a value for; might be null.
	 * @param value The value to set; might be null.
	 */
	<V, VC extends V> void set(Class<V> valueType, VC value);

	/**
	 * Adds a new {@link Configurer} to this builder.
	 * 
	 * @param configurer	A new {@link Configurer} to execute when the builder is executed.
	 * @return this
	 */
	default B configure(Configurer<C> configurer) {
		return configure(configurer, false);
	}

	/**
	 * Adds a new {@link Configurer} to this builder.
	 *
	 * @param configurer	A new {@link Configurer} to execute when the builder is executed; might <b>not</b> be null.
	 * @param prepend		Whether or not to prepend the configurer, so it gets executed before all others.
	 * @return this
	 */
	B configure(Configurer<C> configurer, boolean prepend);

	/**
	 * Returns the {@link Configurer}s currently contained by this {@link EntityBuilder}.
	 * 
	 * @return The current {@link Configurer} list, never null
	 */
	List<Configurer<C>> getConfigurers();

	/**
	 * Returns <code>this</code> in the type of this {@link Object}'s final {@link EntityBuilder} implementation.
	 * 
	 * @return <code>this</code>
	 */
	B getThis();
}