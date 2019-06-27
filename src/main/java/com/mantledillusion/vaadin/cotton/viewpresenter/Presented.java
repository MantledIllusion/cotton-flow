package com.mantledillusion.vaadin.cotton.viewpresenter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PostInject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.vaadin.cotton.viewpresenter.Presentable.PresentValidator;
import com.mantledillusion.vaadin.cotton.viewpresenter.Presentable.PresentProcessor;

/**
 * {@link Annotation} for {@link Presentable} implementations that need controlling by a presenter.
 * <P>
 * Presenters for {@link Presentable}s are instantiated to be completely autonomous, without any possibility to be injected elsewhere.
 */
@Retention(RUNTIME)
@Target(TYPE)
@PreConstruct(PresentValidator.class)
@PostInject(PresentProcessor.class)
public @interface Presented {

	/**
	 * Defines the presenter's implementation type that will be instantiated for instances of the annotated {@link Presentable}.
	 *
	 * @return The presenter implementation that presents instances of a {@link Presentable} implementation; never null
	 */
	Class<?> value();
}