package com.mantledillusion.vaadin.cotton.event;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.mantledillusion.vaadin.cotton.CottonUI;
import com.mantledillusion.vaadin.cotton.event.user.AfterLoginEvent;
import com.mantledillusion.vaadin.cotton.event.user.BeforeLogoutEvent;
import com.mantledillusion.vaadin.cotton.exception.http500.Http500InternalServerErrorException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http903NotImplementedException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http904IllegalAnnotationUseException;
import com.mantledillusion.vaadin.cotton.viewpresenter.Presenter;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

/**
 * Basic super type for a subscriber on the global event bus.
 * <p>
 * NOTE: Should be injected, since the {@link com.mantledillusion.injection.hura.core.Injector}
 * handles the instance's life cycles.
 * <P>
 * The only implementation of the {@link EventBusSubscriber} on framework side
 * is the {@link Presenter}; that being said, an own {@link EventBusSubscriber}
 * implementation subscribes on the same event bus as <B>all</B> presenters of
 * the same {@link VaadinSession}, providing access to the bus' event traffic.
 * <P>
 * All methods of this {@link Presenter} implementation that are annotated
 * with @{@link Subscribe} will receive specifiable events of other
 * {@link EventBusSubscriber}s that were dispatched using the
 * {@link #dispatch(BusEvent)} {@link Method}.
 */
public class EventBusSubscriber {

	/**
	 * Super type for events to dispatch via the global {@link EventBusSubscriber}
	 * event bus.
	 */
	public static abstract class BusEvent {

		private Map<String, String> properties;

		/**
		 * Default {@link Constructor}.
		 */
		protected BusEvent() {
		}

		/**
		 * Convenience {@link Constructor} since most propertied events have 1 property
		 * set.
		 * 
		 * @param key
		 *            The key of the property; <b>not</b> allowed to be null.
		 * @param value
		 *            The value of the property; may be null.
		 */
		protected BusEvent(String key, String value) {
			addProperty(key, value);
		}

		/**
		 * Adds a property to the event. Subscribing {@link EventBusSubscriber}
		 * {@link Method}s may filter events for these properties using
		 * {@link Subscribe.EventProperty}s in the {@link Method}'s {@link Subscribe}
		 * annotation.
		 * 
		 * @param key
		 *            The key of the property; <b>not</b> allowed to be null.
		 * @param value
		 *            The value of the property; may be null.
		 */
		protected final void addProperty(String key, String value) {
			if (key == null) {
				throw new Http901IllegalArgumentException("The key of a property can never be null!");
			}
			if (this.properties == null) {
				this.properties = new HashMap<>();
			}
			this.properties.put(key, value);
		}

		/**
		 * Returns whether this event has ANY value set in this property.
		 * 
		 * @param key
		 *            The key to check for; might be null for convenience, although the
		 *            {@link Method} can only return false in this case since key-less
		 *            properties are not allowed.
		 * @return True when there is a property with the given key, false otherwise
		 */
		public final boolean hasProperty(String key) {
			return this.properties.containsKey(key);
		}

		/**
		 * Returns whether this event has a property with the given key and the value
		 * equals the given one.
		 *
		 * @param key
		 *            The key of the property; might be null for convenience, although
		 *            the {@link Method} can only return false in this case since
		 *            key-less properties are not allowed.
		 * @param value
		 *            The value of the property; may be null.
		 * @return True if there is a property with the given key and the value equals
		 *         the given one, false otherwise
		 */
		public final boolean equalProperty(String key, String value) {
			if (key == null) {
				throw new Http901IllegalArgumentException("The key of a property can never be null!");
			}
			return this.properties.containsKey(key)
					&& (value == null ? this.properties.get(key) == null : this.properties.get(key).equals(value));
		}

		/**
		 * Returns whether this event has properties for all of the given keys and their
		 * values equal the given ones.
		 * <P>
		 * Essentially, this is repeatedly calling
		 * {@link #equalProperty(String, String)} with all entries of the given map.
		 * 
		 * @param properties
		 *            The properties to check against; may be null.
		 * @return True if all properties in the given map equal the properties in this
		 *         event, false otherwise
		 */
		public final boolean equalProperties(Map<String, String> properties) {
			return properties == null || properties.isEmpty()
					|| properties.keySet().stream().allMatch(key -> (properties.get(key) == null
							&& this.properties.containsKey(key) && this.properties.get(key) == null)
							|| (properties.get(key) != null && properties.get(key).equals(this.properties.get(key))));
		}
	}

	// #########################################################################################################################################
	// ############################################################### SUBSCRIBE ###############################################################
	// #########################################################################################################################################

	static class SubscribeValidator implements AnnotationProcessor<Subscribe, Method> {

		@Construct
		private SubscribeValidator() {}

		@Override
		public void process(Phase phase, Object bean, Subscribe annotationInstance, Method annotatedElement,
							Injector.TemporalInjectorCallback callback) {
			Class<?> subscribingType = annotatedElement.getDeclaringClass();

			if (!EventBusSubscriber.class.isAssignableFrom(subscribingType)) {
				throw new Http904IllegalAnnotationUseException("The @" + Subscribe.class.getSimpleName()
						+ " annotation can only be used on " + EventBusSubscriber.class.getSimpleName()
						+ " implementations; the type '" + subscribingType.getSimpleName() + "' however is not.");
			} else if (Modifier.isStatic(annotatedElement.getModifiers())) {
				throw new Http904IllegalAnnotationUseException("The method '" + annotatedElement.getName()
						+ "' of the type '" + subscribingType.getSimpleName() + "' annotated with @"
						+ Subscribe.class.getSimpleName() + " is static, which is not allowed.");
			} else if (annotatedElement.getParameterCount() == 0 && annotationInstance.anonymousEvents().length == 0) {
				throw new Http904IllegalAnnotationUseException("Methods annotated with "
						+ Subscribe.class.getSimpleName()
						+ " are only allowed to have no parameter if there is at least one anonymous event type set; the method '"
						+ annotatedElement.getName() + "' of the type '" + subscribingType.getSimpleName()
						+ "' however has 0 of both.");
			} else if (annotatedElement.getParameterCount() > 1) {
				throw new Http904IllegalAnnotationUseException(
						"Methods annotated with " + Subscribe.class.getSimpleName()
								+ " are only allowed to have a maximum of 1 parameter; the method '"
								+ annotatedElement.getName() + "' of the type '" + subscribingType.getSimpleName()
								+ "' however has " + annotatedElement.getParameterCount());
			}

			if (annotatedElement.getParameterCount() > 0) {
				Class<?> eventType = annotatedElement.getParameterTypes()[0];

				if (!BusEvent.class.isAssignableFrom(eventType)) {
					throw new Http904IllegalAnnotationUseException(
							"Methods annotated with " + Subscribe.class.getSimpleName()
									+ " are only allowed to have 1 parameter that is a sub type of "
									+ BusEvent.class.getSimpleName() + "; the method '" + annotatedElement.getName()
									+ "' of the type '" + subscribingType.getSimpleName()
									+ "' however has 1 parameter of the type '" + eventType.getSimpleName()
									+ "' which is not.");
				}
			}
		}
	}

	// #########################################################################################################################################
	// ################################################################# REACT #################################################################
	// #########################################################################################################################################

	static class ReactValidator implements AnnotationProcessor<React, Method> {

		private static final Set<Class<? extends EventObject>> UI_EVENT_TYPES = Collections
				.unmodifiableSet(new HashSet<>(Arrays.asList(BeforeEnterEvent.class, BeforeLeaveEvent.class,
						AfterNavigationEvent.class, AfterLoginEvent.class, BeforeLogoutEvent.class)));

		@Construct
		private ReactValidator() {}

		@Override
		public void process(Phase phase, Object bean, React annotationInstance, Method annotatedElement,
							Injector.TemporalInjectorCallback callback) {
			Class<?> subscribingType = annotatedElement.getDeclaringClass();

			if (!EventBusSubscriber.class.isAssignableFrom(subscribingType)) {
				throw new Http904IllegalAnnotationUseException("The @" + React.class.getSimpleName()
						+ " annotation can only be used on " + EventBusSubscriber.class.getSimpleName()
						+ " implementations; the type '" + subscribingType.getSimpleName() + "' however is not.");
			} else if (Modifier.isStatic(annotatedElement.getModifiers())) {
				throw new Http904IllegalAnnotationUseException("The method '" + annotatedElement.getName()
						+ "' of the type '" + subscribingType.getSimpleName() + "' annotated with @"
						+ React.class.getSimpleName() + " is static, which is not allowed.");
			} else if (annotatedElement.getParameterCount() == 0) {
				throw new Http904IllegalAnnotationUseException("Methods annotated with " + React.class.getSimpleName()
						+ " are not allowed to have no parameter; the method '" + annotatedElement.getName()
						+ "' of the type '" + subscribingType.getSimpleName() + "' however has none.");
			} else if (annotatedElement.getParameterCount() > 1) {
				throw new Http904IllegalAnnotationUseException("Methods annotated with " + React.class.getSimpleName()
						+ " are only allowed to have a maximum of 1 parameter; the method '"
						+ annotatedElement.getName() + "' of the type '" + subscribingType.getSimpleName()
						+ "' however has " + annotatedElement.getParameterCount());
			}

			if (annotatedElement.getParameterCount() > 0) {
				Class<?> eventType = annotatedElement.getParameterTypes()[0];

				if (!UI_EVENT_TYPES.contains(eventType)) {
					throw new Http904IllegalAnnotationUseException(
							"Methods annotated with " + React.class.getSimpleName()
									+ " are only allowed to have 1 parameter that is one of the types ["
									+ StringUtils.join(UI_EVENT_TYPES, ',') + "]; the method '"
									+ annotatedElement.getName() + "' of the type '" + subscribingType.getSimpleName()
									+ "' however has 1 parameter of the type '" + eventType.getSimpleName()
									+ "' which is not.");
				}
			}
		}
	}

	// #########################################################################################################################################
	// ################################################################## TYPE #################################################################
	// #########################################################################################################################################

	@Inject
	@Qualifier(EventBus.SID_EVENTBUS)
	private EventBus bus;

	private List<Registration> uiListenerRegistrations = new ArrayList<>();

	@PostInject
	private void initialize() {
		// PRESENTER EVENT METHODS
		for (Method method : MethodUtils.getMethodsListWithAnnotation(getClass(), Subscribe.class, true, true)) {
			makeAccessible(method);

			Subscribe annotation = method.getAnnotation(Subscribe.class);

			Map<String, String> properties = null;
			if (annotation.value().length > 0) {
				properties = new HashMap<>();
				for (Subscribe.EventProperty property : annotation.value()) {
					if (properties.containsKey(property.key())) {
						throw new Http904IllegalAnnotationUseException(
								"The event property key '" + property.key() + "' is used twice.");
					}
					properties.put(property.key(), property.value());
				}
			}

			if (method.getParameterCount() > 0) {
				Class<?> eventType = method.getParameterTypes()[0];

				@SuppressWarnings("unchecked")
				Class<? extends BusEvent> parameterEventType = (Class<? extends BusEvent>) eventType;

				this.bus.subscribe(parameterEventType, this, method, true, properties, annotation.isSelfObservant());
			}

			for (Class<? extends BusEvent> anonymousEventType : annotation.anonymousEvents()) {
				this.bus.subscribe(anonymousEventType, this, method, false, properties, annotation.isSelfObservant());
			}
		}

		// UI EVENT METHODS
		for (Method method : MethodUtils.getMethodsListWithAnnotation(getClass(), React.class, true, true)) {
			makeAccessible(method);

			Class<?> eventType = method.getParameterTypes()[0];

			Registration reg;
			if (BeforeLeaveEvent.class.isAssignableFrom(eventType)) {
				reg = CottonUI.current().addBeforeLeaveListener(event -> invoke(method, event));
			} else if (BeforeEnterEvent.class.isAssignableFrom(eventType)) {
				reg = CottonUI.current().addBeforeEnterListener(event -> invoke(method, event));
			} else if (AfterNavigationEvent.class.isAssignableFrom(eventType)) {
				reg = CottonUI.current().addAfterNavigationListener(event -> invoke(method, event));
			} else if (AfterLoginEvent.class.isAssignableFrom(eventType)) {
				reg = CottonUI.current().addAfterLoginListener(event -> invoke(method, event));
			} else if (BeforeLogoutEvent.class.isAssignableFrom(eventType)) {
				reg = CottonUI.current().addBeforeLogoutListener(event -> invoke(method, event));
			} else {
				throw new Http903NotImplementedException("Unable to register the method " + method.getName()
						+ " to react on the unknown event type " + eventType.getSimpleName());
			}
			this.uiListenerRegistrations.add(reg);
		}
	}

	private void makeAccessible(Method method) {
		if (!method.isAccessible()) {
			try {
				method.setAccessible(true);
			} catch (SecurityException e) {
				throw new Http904IllegalAnnotationUseException(
						"Unable to gain access to the method '" + method.getName() + "' of the type "
								+ EventBusSubscriber.this.getClass().getSimpleName() + " which is inaccessible.",
						e);
			}
		}
	}

	private void invoke(Method m, EventObject event) {
		try {
			m.invoke(this, event);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new Http500InternalServerErrorException("Unable to dispatch event of type "
					+ event.getClass().getSimpleName() + " to a subscriber of the type " + getClass().getSimpleName(),
					e);
		}
	}

	@PreDestroy
	private void releaseReferences() {
		this.bus.unsubscribe(this);
		this.uiListenerRegistrations.forEach(reg -> reg.remove());
		this.uiListenerRegistrations.clear();
	}

	/**
	 * Dispatches the given {@link BusEvent} through the global event bus that links
	 * all {@link EventBusSubscriber}s via the {@link Subscribe} annotation on their
	 * {@link Method}s.
	 * 
	 * @param event
	 *            The event to dispatch; <b>not</b> allowed to be null.
	 * @return True if the {@link BusEvent} has been received by at least one
	 *         {@link EventBusSubscriber}, false otherwise
	 */
	protected final boolean dispatch(BusEvent event) {
		return this.bus.dispatch(event, this);
	}
}
