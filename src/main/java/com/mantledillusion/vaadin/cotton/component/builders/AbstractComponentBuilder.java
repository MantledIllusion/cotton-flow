package com.mantledillusion.vaadin.cotton.component.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.vaadin.flow.component.Component;

/**
 * Base implementation of {@link ComponentBuilder} that provides all of the base functionality except {@link ComponentBuilder#instantiate()};
 *
 * @param <C>
 *            The {@link Component} type this builder builds. Not an extension
 *            of {@link Component} on purpose, since Vaadin handles shared
 *            {@link Component} behavior using interfaces that are not
 *            necessarily bound to {@link Component}s.
 * @param <B>
 *            The final implementation type of this {@link AbstractComponentBuilder}.
 *            Necessary to allow builder methods of non-final implementations to
 *            return the builder instance in the correct type.
 */
public abstract class AbstractComponentBuilder<C, B extends AbstractComponentBuilder<C, B>> implements ComponentBuilder<C, B> {
	
	private final List<Configurer<C>> configurators = new ArrayList<>();
	
	@Override
	public B configure(Configurer<C> configurer) {
		this.configurators.add(configurer);
		return getThis();
	}
	
	@Override
	public List<Configurer<C>> getConfigurers() {
		return Collections.unmodifiableList(this.configurators);
	}

	@Override
	@SuppressWarnings("unchecked")
	public B getThis() {
		return (B) this;
	}
	
	@Override
	public C build() {
		C component = instantiate();
		this.configurators.forEach(configuration -> configuration.configure(component));
		return component;
	}
}