package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.EntityBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base implementation of {@link ComponentBuilder} that provides all of the base functionality.;
 *
 * @param <C>
 *            The entity type this builder builds.
 * @param <B>
 *            The final implementation type of this {@link AbstractEntityBuilder}. Necessary to allow builder methods of
 *            non-final implementations to return the builder instance in the correct type.
 */
abstract class AbstractEntityBuilder<C, B extends AbstractEntityBuilder<C, B>> implements EntityBuilder<C, B> {
	
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

	protected void apply(C entity) {
		this.configurators.forEach(configuration -> configuration.configure(entity));
	}
}