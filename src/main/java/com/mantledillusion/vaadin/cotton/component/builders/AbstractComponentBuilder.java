package com.mantledillusion.vaadin.cotton.component.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;

abstract class AbstractComponentBuilder<C, B extends AbstractComponentBuilder<C, B>> implements ComponentBuilder<C, B> {
	
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