package com.mantledillusion.vaadin.cotton.viewpresenter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Component;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mantledillusion.vaadin.cotton.User;

/**
 * {@link Annotation} for any @{@link Route}d {@link Component}s that requires a logged in {@link User} with certain
 * rights in order to be displayed.
 */
@Retention(RUNTIME)
@Target(TYPE)
@PreConstruct(RoutedValidator.class)
public @interface Restricted {

	/**
	 * Defines the rightIds of the rights the {@link User} has to own upon navigating to the {@link com.vaadin.flow.router.Route}.
	 * <P>
	 * If no rightIds are specified a {@link User} just has to be logged in.
	 * 
	 * @return The rightIds the logged in user has to have to be allowed to view the annotated {@link Presentable}; never null,
	 * might be empty, empty by default
	 */
	String[] value() default {};
}