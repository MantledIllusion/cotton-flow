package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.viewpresenter.Presentable;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;

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
abstract class AbstractComponentBuilder<C extends Component, B extends AbstractComponentBuilder<C, B>>
		extends AbstractEntityBuilder<C, B> implements ComponentBuilder<C, B> {

	/**
	 * Builder method, configures a {@link Component}'s id.
	 *
	 * @see Component#setId(String)
	 * @param id The id to set; might <b>not</b> be null.
	 * @return this
	 */
	public final B setId(String id) {
		return configure(activeComponent -> activeComponent.setId(id));
	}

	/**
	 * Builder method, registers a {@link Component} to a {@link Presentable.TemporalActiveComponentRegistry} by its id.
	 *
	 * @see Component#setId(String)
	 * @see Presentable.TemporalActiveComponentRegistry#register(Component)
	 * @param activeComponentRegistry The {@link Presentable.TemporalActiveComponentRegistry} to register at;
	 *                                might <b>not</b> be null.
	 * @return this
	 */
	public final B setRegistration(Presentable.TemporalActiveComponentRegistry activeComponentRegistry) {
		return setRegistration(activeComponentRegistry, null);
	}

	/**
	 * Builder method, registers a {@link Component} to a {@link Presentable.TemporalActiveComponentRegistry} by its id.
	 *
	 * @see Component#setId(String)
	 * @see Presentable.TemporalActiveComponentRegistry#register(Component)
	 * @param activeComponentRegistry The {@link Presentable.TemporalActiveComponentRegistry} to register at;
	 *                                might <b>not</b> be null.
	 * @param id The id to set; might be null.
	 * @return this
	 */
	public final B setRegistration(Presentable.TemporalActiveComponentRegistry activeComponentRegistry, String id) {
		if (activeComponentRegistry == null) {
			throw new Http901IllegalArgumentException("Cannot register a component at a null active component registry");
		}
		return configure(activeComponent -> activeComponentRegistry.register(activeComponent, id));
	}

	/**
	 * Adds a listener for {@link AttachEvent}s.
	 *
	 * @see Component#addAttachListener(ComponentEventListener)
	 * @param listener The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	public final B addAttachListener(ComponentEventListener<AttachEvent> listener) {
		return configure(component -> component.addAttachListener(listener));
	}

	/**
	 * Adds a listener for {@link DetachEvent}s.
	 *
	 * @see Component#addDetachListener(ComponentEventListener)
	 * @param listener The listener to add; might <b>not</b> be null.
	 * @return this
	 */
	public final B addDetachListener(ComponentEventListener<DetachEvent> listener) {
		return configure(component -> component.addDetachListener(listener));
	}

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