package com.mantledillusion.vaadin.cotton.component;

import java.util.List;

import com.vaadin.flow.component.Component;

/**
 * Interface for {@link Component} builders.
 *
 * @param <C>
 *            The {@link Component} type this builder builds. Not an extension
 *            of {@link Component} on purpose, since Vaadin handles shared
 *            {@link Component} behavior using interfaces that are not
 *            necessarily bound to {@link Component}s.
 * @param <B>
 *            The final implementation type of this {@link ComponentBuilder}.
 *            Necessary to allow builder methods of non-final implementations to
 *            return the builder instance in the correct type.
 */
public interface ComponentBuilder<C, B extends ComponentBuilder<C, B>> {

	/**
	 * Instantiates a new instance of the {@link Component} to build on every
	 * invocation.
	 * 
	 * @return A new instance of the {@link Component} to build, never null
	 */
	C instantiate();

	/**
	 * Adds a new {@link Configurer} to this builder.
	 * 
	 * @param configurer
	 *            A new {@link Configurer} to execute sequentially when
	 *            {@link #build()} is called.
	 * @return this
	 */
	B configure(Configurer<C> configurer);

	/**
	 * Returns the {@link Configurer}s currently contained by this
	 * {@link ComponentBuilder}.
	 * 
	 * @return The current {@link Configurer} list, never null
	 */
	List<Configurer<C>> getConfigurers();

	/**
	 * Returns <code>this</code> in the type of this {@link Object}'s final
	 * {@link ComponentBuilder} implementation.
	 * 
	 * @return <code>this</code>
	 */
	B getThis();

	/**
	 * Creates a new {@link Component} instance using {@link #instantiate()},
	 * applies all currently contained {@link Configurer}s to it and returns it.
	 * 
	 * @return A new {@link Component} instance, fully configured, never null
	 */
	C build();
}