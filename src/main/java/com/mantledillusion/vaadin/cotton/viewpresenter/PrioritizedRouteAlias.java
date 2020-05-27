package com.mantledillusion.vaadin.cotton.viewpresenter;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.Component;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for any @{@link Route}d {@link Component}, offering to enter an additional path to access the
 * {@link Component} under, with the addition to @{@link RouteAlias} that there can be multiple {@link Component}
 * registered for the same path.
 * <p>
 * Which of the {@link Component}s is accessed depends on the {@link #priority()} set, and whether a
 * possible @{@link Restricted} annotation's authentication and authorization demands are met.
 * <p>
 * For example if 3 {@link Component}s have {@link PrioritizedRouteAlias}es for the same path...<br>
 * - 1: priority=0, restricted to a user being authenticated and owning a certain right<br>
 * - 2: priority=1, restricted to a user being authenticated<br>
 * - 3: priority=2, not restricted at all<br>
 * ...the {@link Component} to use will depend completely on the users rights.
 * <p>
 * Note that it is possible to use the same priority for multiple {@link Component}s on the same path, since a random
 * outcome could still be prevented by setting right restrictions accordingly. If not and such a path is accessed, the
 * {@link Component} visited <u>will be random</u>.
 */
@Retention(RUNTIME)
@Target(TYPE)
@Repeatable(PrioritizedRouteAlias.PrioritizedRouteAliases.class)
@PreConstruct(PrioritizedRouteAliasValidator.class)
public @interface PrioritizedRouteAlias {

    /**
     * Internal annotation to enable use of multiple {@link PrioritizedRouteAlias} annotations.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface PrioritizedRouteAliases {

        /**
         * Internally used to enable use of multiple {@link PrioritizedRouteAlias} annotations.
         *
         * @return An array of {@link PrioritizedRouteAlias} annotations, never null, might be empty.
         */
        PrioritizedRouteAlias[] value();
    }

    /**
     * Returns the alias path.
     *
     * @see RouteAlias#value()
     * @return The path, never null
     */
    String value();

    /**
     * Determines the priority in which the annotated {@link Component} aligns with other {@link Component}s using the
     * same path.
     * <p>
     * Lower values mean higher priority.
     *
     * @return The priority
     */
    int priority() default 0;
}
