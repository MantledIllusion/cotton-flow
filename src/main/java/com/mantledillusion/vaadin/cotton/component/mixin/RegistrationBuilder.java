package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.viewpresenter.Presentable;
import com.vaadin.flow.component.Component;

/**
 * {@link EntityBuilder} for {@link Component}s to gester at an {@link Presentable.TemporalActiveComponentRegistry}.
 *
 * @param <C>
 *            The {@link Component} type.
 * @param <B>
 *            The final implementation type of {@link RegistrationBuilder}.
 */
public interface RegistrationBuilder<C extends Component, B extends RegistrationBuilder<C, B>> extends EntityBuilder<C, B> {

    /**
     * Builder method, registers a {@link Component} to a {@link Presentable.TemporalActiveComponentRegistry} by its id.
     *
     * @see Component#setId(String)
     * @see Presentable.TemporalActiveComponentRegistry#register(Component)
     * @param activeComponentRegistry The {@link Presentable.TemporalActiveComponentRegistry} to register at;
     *                                might <b>not</b> be null.
     * @return this
     */
    default B setRegistration(Presentable.TemporalActiveComponentRegistry activeComponentRegistry) {
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
    default B setRegistration(Presentable.TemporalActiveComponentRegistry activeComponentRegistry, String id) {
        if (activeComponentRegistry == null) {
            throw new Http901IllegalArgumentException("Cannot register a component at a null active component registry");
        }
        return configure(activeComponent -> activeComponentRegistry.register(activeComponent, id));
    }
}
