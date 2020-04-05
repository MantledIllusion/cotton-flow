package com.mantledillusion.vaadin.cotton.viewpresenter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.vaadin.cotton.viewpresenter.Presentable.PresentedValidator;

/**
 * {@link Annotation} for {@link Presentable} implementations that need controlling by a {@link Presenter}.
 * <P>
 * {@link Presenter}s for {@link Presentable}s are instantiated to be completely autonomous, without any possibility to
 * be injected elsewhere.
 */
@Retention(RUNTIME)
@Target(TYPE)
@PreConstruct(PresentedValidator.class)
public @interface Presented {

	/**
	 * Defines the {@link Presenter}'s implementation type that will be instantiated for instances of the annotated
	 * {@link Presentable}.
	 *
	 * @return The {@link Presenter} implementation that presents instances of a {@link Presentable} implementation;
	 * never null
	 */
	Class<? extends Presenter<?>> value();
}