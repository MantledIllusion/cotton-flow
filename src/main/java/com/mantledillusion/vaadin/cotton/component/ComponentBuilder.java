package com.mantledillusion.vaadin.cotton.component;

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
public interface ComponentBuilder<C, B extends ComponentBuilder<C, B>> extends EntityBuilder<C, B> {

	/**
	 * Creates a new {@link Component} instance, applies all currently contained {@link Configurer}s to it and returns it.
	 *
	 * @return A new {@link Component} instance, fully configured, never null
	 */
	C build();
}