package com.mantledillusion.vaadin.cotton.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.mantledillusion.injection.hura.annotation.Validated;
import com.mantledillusion.vaadin.cotton.event.EventBusSubscriber.ReactValidator;
import com.mantledillusion.vaadin.cotton.event.user.AfterLoginEvent;
import com.mantledillusion.vaadin.cotton.event.user.BeforeLogoutEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;

/**
 * {@link Annotation} for {@link Method}s of {@link EventBusSubscriber}
 * implementations that have to react when specific events thrown by the
 * {@link UI} are dispatched.
 * 
 * <P>
 * An annotated {@link Method} is expected to be a void {@link Method} and has
 * to receive exactly 1 {@link Parameter} of a one of the following events, or
 * have at least one of those types set as anonymous event type:<br>
 * - {@link BeforeLeaveEvent}<br>
 * - {@link BeforeEnterEvent}<br>
 * - {@link AfterNavigationEvent}<br>
 * - {@link AfterLoginEvent}<br>
 * - {@link BeforeLogoutEvent}
 * <P>
 * The {@link Method} will be called for events of that {@link Parameter}'s
 * type.
 */
@Retention(RUNTIME)
@Target(METHOD)
@Validated(ReactValidator.class)
public @interface React {

}
