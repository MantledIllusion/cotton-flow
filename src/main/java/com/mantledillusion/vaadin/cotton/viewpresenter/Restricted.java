package com.mantledillusion.vaadin.cotton.viewpresenter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PostInject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.essentials.expression.Expression;
import com.mantledillusion.vaadin.cotton.exception.http400.Http403UnauthorizedException;
import com.mantledillusion.vaadin.cotton.LoginProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Component;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mantledillusion.vaadin.cotton.User;

/**
 * {@link Annotation} for any  {@link Component} that requires {@link User} authorization of some sort determining
 * whether that {@link User} is granted access to the annotated instance.
 * <p>
 * This {@link Annotation} has two effects :<br>
 * - On every {@link Component} it is found on, {@link Component#setVisible(boolean)} will be used to hide it if
 * rights are not sufficient for access to be granted<br>
 * - On {@link Component}s that are also annotated with @{@link Route}, the configured {@link LoginProvider} is
 * triggered to cause a login process if no {@link User} is currently logged in; if a {@link User} is logged in but the
 * rights are not sufficient access will be denied completely by throwing a {@link Http403UnauthorizedException}
 */
@Retention(RUNTIME)
@Target(TYPE)
@PreConstruct(RestrictedValidator.class)
@PostInject(RestrictedProcessor.class)
public @interface Restricted {

	/**
	 * Defines a parsable {@link Expression} of right IDs that has to validate true against a logged in {@link User} in
	 * order for access to be granted.
	 * <p>
	 * For example, if a {@link User} has to own the right "R1" and either the right "R2" or "R3", the
	 * {@link Expression} might look like this: <code>@Restricted("R1 &amp;&amp; (R2 || R3)")</code>
	 * <p>
	 * Note that leaving this value empty will grant access to any authenticated {@link User}.
	 * 
	 * @return The right ID based expression to validate; never null, be empty, has to be parsable by
	 * {@link Expression#parse(String)}
	 */
	String value() default "";
}