package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.essentials.expression.Expression;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.model.AuditingConfigurer;
import com.mantledillusion.vaadin.cotton.model.Binding;
import com.vaadin.flow.component.Component;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.Function;

/**
 * Builder to configure a {@link Binding} of a {@link Component} being build with with.
 *
 * @param <C> The bound component type.
 */
public final class BindingBuilder<C> implements AuditingConfigurer<BindingBuilder<C>> {

    private final ComponentBuilder<C, ?> componentBuilder;
    private final Function<C, Binding<?>> bindingCallback;

    private final List<Triple<Binding.AccessMode, Boolean, Expression<String>>> bindingAuditors = new ArrayList<>();

    public BindingBuilder(ComponentBuilder<C, ?> componentBuilder, Function<C, Binding<?>> bindingCallback) {
        this.componentBuilder = componentBuilder;
        this.bindingCallback = bindingCallback;
    }

    @Override
    public BindingBuilder<C> setAudit(Binding.AccessMode mode, boolean requiresLogin, Expression<String> rightExpression) {
        this.bindingAuditors.add(Triple.of(mode, requiresLogin, rightExpression));
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
        this.bindingAuditors.forEach(auditor -> binding.setAudit(auditor.getLeft(), auditor.getRight()));

        return component;
    }
}
