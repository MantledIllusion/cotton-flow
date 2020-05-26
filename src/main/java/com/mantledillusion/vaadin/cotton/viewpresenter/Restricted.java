package com.mantledillusion.vaadin.cotton.viewpresenter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.essentials.expression.Expression;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Component;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mantledillusion.vaadin.cotton.User;

/**
 * {@link Annotation} for any @{@link Route}d {@link Component}s that requires a logged in {@link User} to own a
 * specifiable constellation of right IDs.
 */
@Retention(RUNTIME)
@Target(TYPE)
@PreConstruct(RoutedValidator.class)
public @interface Restricted {

	/**
	 * Defines a parsable {@link Expression} of right IDs that has to validate true against a logged in {@link User} in
	 * order to be allowed assess to the {@link com.vaadin.flow.router.Route}.
	 * <p>
	 * For example, if a {@link User} has to own the right "R1" and either the right "R2" or "R3", the
	 * {@link Expression} might look like this: <code>@Restricted("R1 &amp;&amp; (R2 || R3)")</code>
	 * <p>
	 * If no expression is specified a {@link User} just has to be logged in in order to gain access.
	 * 
	 * @return The right ID based expression to validate; never null, be empty, has to be parsable by
	 * {@link Expression#parse(String)}
	 */
	String value() default "";
}