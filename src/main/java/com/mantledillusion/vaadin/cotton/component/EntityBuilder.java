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
	 * Adds a new {@link Configurer} to this builder.
	 * 
	 * @param configurer	A new {@link Configurer} to execute when the builder is executed.
	 * @return this
	 */
	B configure(Configurer<C> configurer);

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