package com.mantledillusion.vaadin.cotton.viewpresenter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;

import com.mantledillusion.vaadin.cotton.exception.http900.Http904IllegalAnnotationUseException;
import com.mantledillusion.vaadin.cotton.viewpresenter.View.TemporalActiveComponentRegistry;
import com.vaadin.flow.component.Component;

/**
 * Basic super type for a presenter that controls an {@link View}.
 * <p>
 * NOTE: Should be injected, since the {@link com.mantledillusion.injection.hura.core.Injector} handles the instance's
 * life cycles.
 * <P>
 * Instances of sub types of {@link AbstractPresenter} will be instantiated automatically during injection for every {@link View}
 * implementation that requires controlling by an @{@link Presented} annotation on that view type.
 * <P>
 * The {@link AbstractPresenter} will automatically be connected to the view it belongs to, that view can be retrieved by
 * calling {@link #getView()}.
 * <P>
 * All {@link Method}s of this {@link AbstractPresenter} implementation that are annotated with @{@link Listen} will receive
 * specifiable events of {@link Component}s on the connected view that have been registered as active component to
 * the {@link TemporalActiveComponentRegistry} during the view's UI build; see the documentation of @{@link Listen} for
 * details.
 *
 * @param <V>
 *            The type of {@link View} this {@link AbstractPresenter} can control.
 */
public abstract class AbstractPresenter<V extends View> {

	static class ListenValidator implements AnnotationProcessor<Listen, Method> {

		@Construct
		private ListenValidator() {}

		@Override
		public void process(Phase phase, Object bean, Listen annotationInstance, Method annotatedElement,
							Injector.TemporalInjectorCallback callback) {
			Class<?> listeningType = annotatedElement.getDeclaringClass();

			if (Modifier.isStatic(annotatedElement.getModifiers())) {
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

	private V view;

	protected final V getView() {
		return view;
	}

	@SuppressWarnings("unchecked")
	final void setView(V view) {
		this.view = view;
	}
}
