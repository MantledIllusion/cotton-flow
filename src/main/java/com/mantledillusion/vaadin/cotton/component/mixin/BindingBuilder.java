package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.model.Binding;
import com.vaadin.flow.component.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builder to configure a {@link Binding} of a {@link Component} being build with with.
 *
 * @param <C> The bound component type.
 */
public final class BindingBuilder<C> {

    private final ComponentBuilder<C, ?> componentBuilder;
    private final Function<C, Binding<?>> bindingCallback;

    private final List<Supplier<Binding.AccessMode>> bindingAuditors = new ArrayList<>();

    public BindingBuilder(ComponentBuilder<C, ?> componentBuilder, Function<C, Binding<?>> bindingCallback) {
        this.componentBuilder = componentBuilder;
        this.bindingCallback = bindingCallback;
    }

    /**
     * Builder method, adds the given binding auditor to the binding to restrict it.
     *
     * @see Binding#withRestriction(Supplier)
     * @param bindingAuditor The binding auditor; might <b>not</b> be null.
     * @return this
     */
    public BindingBuilder<C> withRestriction(Supplier<Binding.AccessMode> bindingAuditor) {
        this.bindingAuditors.add(bindingAuditor);
        return this;
    }

    /**
     * Creates a new {@link Component} instance using {@link ComponentBuilder#build()}, applies all currently
     * contained {@link Configurer}s to it and returns it.
     *
     * @see ComponentBuilder#build()
     * @return A new {@link Component} instance, fully configured and bound, never null
     */
    public C bind() {
        C component = componentBuilder.build();

        // APPLY ALL BINDING AUDITORS
        Binding<?> binding = this.bindingCallback.apply(component);
        this.bindingAuditors.forEach(binding::withRestriction);

        return component;
    }
}
