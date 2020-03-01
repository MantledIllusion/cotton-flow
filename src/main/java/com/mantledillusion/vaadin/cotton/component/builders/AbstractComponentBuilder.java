package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.vaadin.flow.component.Component;

/**
 * Base implementation of {@link ComponentBuilder} that provides all of the base functionality except
 * {@link AbstractComponentBuilder#instantiate()};
 *
 * @param <C>
 *            The {@link Component} type this builder builds. Not an extension of {@link Component} on purpose, since
 *            Vaadin handles shared {@link Component} behavior using interfaces that are not necessarily bound to
 *            {@link Component}s.
 * @param <B>
 *            The final implementation type of this {@link AbstractComponentBuilder}. Necessary to allow builder
 *            methods of non-final implementations to return the builder instance in the correct type.
 */
abstract class AbstractComponentBuilder<C, B extends AbstractComponentBuilder<C, B>>
		extends AbstractEntityBuilder<C, B> implements ComponentBuilder<C, B> {

	/**
	 * Creates a new {@link Component} instance using {@link #instantiate()}, applies all currently contained
	 * {@link Configurer}s to it and returns it.
	 *
	 * @return A new {@link Component} instance, fully configured, never null
	 */
	@Override
	public C build() {
		C component = instantiate();
		apply(component);
		return component;
	}

	/**
	 * Instantiates a new instance of the {@link Component} to build on every invocation.
	 *
	 * @return A new instance of the {@link Component} to build, never null
	 */
	protected abstract C instantiate();
}