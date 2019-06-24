package com.mantledillusion.vaadin.cotton.viewpresenter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.mantledillusion.vaadin.cotton.exception.http900.Http904IllegalAnnotationUseException;
import com.mantledillusion.vaadin.cotton.viewpresenter.View.TemporalActiveComponentRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

/**
 * Basic super type for a presenter that controls an {@link View}.
 * <p>
 * NOTE: Should be injected, since the {@link com.mantledillusion.injection.hura.core.Injector}
 * handles the instance's life cycles.
 * <P>
 * Instances of sub types of {@link Presenter} will be instantiated
 * automatically during injection for every {@link View} implementation that
 * requires controlling by an @{@link Presented} annotation on that view type.
 * <P>
 * The {@link Presenter} will automatically be connected to the view it belongs
 * to, that view can be retrieved by calling {@link #getView()}.
 * <P>
 * All {@link Method}s of this {@link Presenter} implementation that are
 * annotated with @{@link Listen} will receive specifiable events of
 * {@link Component}s on the connected view that have been registered as
 * active component to the {@link TemporalActiveComponentRegistry} during the
 * view's UI build; see the documentation of @{@link Listen} for details.
 *
 * @param <T>
 *            The type of {@link View} this {@link Presenter} can control.
 */
public abstract class Presenter<T extends View> {

	// #########################################################################################################################################
	// ################################################################ LISTEN #################################################################
	// #########################################################################################################################################

	static class ListenValidator implements AnnotationProcessor<Listen, Method> {

		@Override
		public void process(Phase phase, Object bean, Listen annotationInstance, Method annotatedElement,
							Injector.TemporalInjectorCallback callback) {
			Class<?> listeningType = annotatedElement.getDeclaringClass();

			if (!Presenter.class.isAssignableFrom(listeningType)) {
				throw new Http904IllegalAnnotationUseException(
						"The @" + Listen.class.getSimpleName() + " annotation can only be used on "
								+ Presenter.class.getSimpleName() + " implementations; the type '"
								+ listeningType.getSimpleName() + "' however is not.");
			} else if (Modifier.isStatic(annotatedElement.getModifiers())) {
				throw new Http904IllegalAnnotationUseException(
						"The method '" + annotatedElement.getName() + "' of the type '" + listeningType.getSimpleName()
								+ "' annotated with @" + Listen.class.getSimpleName()
								+ " is static, which is not allowed.");
			} else if (annotatedElement.getParameterCount() == 0 && annotationInstance.anonymousEvents().length == 0) {
				throw new Http904IllegalAnnotationUseException( "Methods annotated with @"
						+ Listen.class.getSimpleName()
						+ " are only allowed to have no parameter if there is at least one anonymous event type set; the method '"
						+ annotatedElement.getName() + "' of the type '" + listeningType.getSimpleName()
						+ "' however has 0 of both.");
			} else if (annotatedElement.getParameterCount() > 1) {
				throw new Http904IllegalAnnotationUseException(
						"Methods annotated with @" + Listen.class.getSimpleName()
								+ " are only allowed to have 0 or 1 parameters. The method "
								+ annotatedElement.getName() + " of the type '" + listeningType.getSimpleName()
								+ "' however has " + annotatedElement.getParameterCount());
			}
		}
	}

	// #########################################################################################################################################
	// ################################################################## TYPE #################################################################
	// #########################################################################################################################################

	private T view;

	protected final T getView() {
		return view;
	}

	@SuppressWarnings("unchecked")
	final void setView(T view, TemporalActiveComponentRegistry reg) {
		this.view = view;

		for (Method method : MethodUtils.getMethodsListWithAnnotation(getClass(), Listen.class, true, true)) {
			// COMPONENT EVENT METHODS
			if (method.isAnnotationPresent(Listen.class)) {
				if (!method.isAccessible()) {
					try {
						method.setAccessible(true);
					} catch (SecurityException e) {
						throw new Http904IllegalAnnotationUseException(
								"Unable to gain access to the method '" + method.getName() + "' of the type "
										+ Presenter.this.getClass().getSimpleName() + ".",
								e);
					}
				}

				Listen annotation = method.getAnnotation(Listen.class);

				List<Class<? extends ComponentEvent<?>>> eventTypes = new ArrayList<>(Arrays.asList(annotation.anonymousEvents()));
				if (method.getParameterCount() > 0) {
					eventTypes.add((Class<? extends ComponentEvent<?>>) method.getParameterTypes()[0]);
				}

				for (Class<? extends ComponentEvent<?>> eventType: eventTypes) {
					if (annotation.value().length == 0) {
						reg.addListener(null, eventType, this, method);
					} else {
						for (String componentId : annotation.value()) {
							reg.addListener(componentId, eventType, this, method);
						}
					}
				}
			}
		}
	}
}
