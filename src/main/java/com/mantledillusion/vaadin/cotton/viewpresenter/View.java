package com.mantledillusion.vaadin.cotton.viewpresenter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.PhasedBeanProcessor;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.BeanProcessor;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.vaadin.cotton.exception.http500.Http500InternalServerErrorException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http902IllegalStateException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http903NotImplementedException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http904IllegalAnnotationUseException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http906InjectionErrorException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;

/**
 * Basic super type for a view.
 * <p>
 * NOTE: Should be injected, since the {@link com.mantledillusion.injection.hura.core.Injector} handles the instance's
 * life cycles.
 * <p>
 * Might be controlled by an {@link Presenter} implementation
 * using @{@link Presented} on the {@link View} implementing type; see the
 * documentation of @{@link Presented} for reference.
 */
public abstract class View extends Composite<Div> {

	private static final long serialVersionUID = 1L;

	// #########################################################################################################################################
	// ################################################################ PRESENT ################################################################
	// #########################################################################################################################################

	static class PresentValidator implements AnnotationProcessor<Presented, Class<?>> {

		@Override
		public void process(Phase phase, Object bean, Presented annotationInstance, Class<?> annotatedElement, Injector.TemporalInjectorCallback callback) {
			if (!View.class.isAssignableFrom(annotatedElement)) {
				throw new Http904IllegalAnnotationUseException("The @" + Presented.class.getSimpleName()
						+ " annotation can only be used on " + View.class.getSimpleName()
						+ " implementations; the type '" + annotatedElement.getSimpleName() + "' however is not.");
			}
		}
	}

	// #########################################################################################################################################
	// ########################################################## COMPONENT REGISTRY ###########################################################
	// #########################################################################################################################################

	/**
	 * Temporarily active registry for {@link Component}s on an {@link View} that
	 * are active (fire component events that the controlling {@link Presenter} has
	 * to react on).
	 * <P>
	 * May only be used during the initialization of the {@link View} it is given
	 * to.
	 */
	protected final class TemporalActiveComponentRegistry {

		private final Map<String, List<Component>> activeComponents = new HashMap<>();
		private boolean canRegister = true;

		/**
		 * Registers the given {@link Component} with the given componentId, which will
		 * make the component's events listenable to for {@link Presenter} methods
		 * annotated with @Listen.
		 * 
		 * @param             <T> The type of the {@link Component} to register.
		 * @param componentId The componentId to register an {@link Component} under;
		 *                    <b>not</b> allowed to be null.
		 * @param component   The component to register; <b>not</b> allowed to be null.
		 * @return The given component, for inline building
		 */
		public <T extends Component> T registerActiveComponent(String componentId, T component) {
			if (!canRegister) {
				throw new Http902IllegalStateException(
						"The component registry may only be used during the initialization of the view it is given to, "
								+ "as components registered later would not be linked to the view's contolling subscriber anymore.");
			} else if (componentId == null) {
				throw new Http901IllegalArgumentException("Cannot register a component with a null componentId.");
			} else if (component == null) {
				throw new Http901IllegalArgumentException("Cannot register a null component.");
			} else {
				if (!this.activeComponents.containsKey(componentId)) {
					this.activeComponents.put(componentId, new ArrayList<>());
				}
				this.activeComponents.get(componentId).add(component);

				return component;
			}
		}

		<T extends ComponentEvent<?>> void addListener(String componentId, Class<T> eventType, Presenter<?> presenter,
				Method m) {
			if (componentId == null) {
				for (List<Component> components : this.activeComponents.values()) {
					for (Component component : components) {
						routeEvent(component, eventType, m, presenter);
					}
				}
			} else if (this.activeComponents.containsKey(componentId)) {
				for (Component component : this.activeComponents.get(componentId)) {
					routeEvent(component, eventType, m, presenter);
				}
			} else {
				throw new Http902IllegalStateException("There is no component named '" + componentId
						+ "' registered in the view " + getClass().getSimpleName());
			}
		}

		<T extends ComponentEvent<?>> void routeEvent(Component c, Class<T> eventType, Method m,
				Presenter<?> presenter) {
			ComponentUtil.addListener(c, eventType, event -> {
				try {
					m.invoke(presenter, event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new Http500InternalServerErrorException(
							"The method '" + m.getName() + "' failed to handle the event '" + event + "'", e);
				}
			});
		}
	}

	// #########################################################################################################################################
	// ################################################################# TYPE
	// ##################################################################
	// #########################################################################################################################################

	private Component root;

	@SuppressWarnings("rawtypes")
	@PostInject
	private <T2 extends View, T3 extends Presenter<T2>> void initialize(Injector.TemporalInjectorCallback callback) {

		TemporalActiveComponentRegistry reg = setupUI();

		if (getClass().isAnnotationPresent(Presented.class)) {

			@SuppressWarnings("unchecked")
			Class<T3> presenterType = (Class<T3>) getClass().getAnnotation(Presented.class).value();

			BeanProcessor<T3> postProcessor = (phase, bean, injectorCallback) -> {
				try {
					bean.setView((T2) View.this, reg);
				} catch (Exception e) {
					throw new Http906InjectionErrorException("The view type " + View.this.getClass().getSimpleName()
							+ " is wired to the subscriber type " + bean.getClass().getSimpleName()
							+ "; setting an instance of that view on an instance of that subscriber however failed.",
							e);
				}
			};

			callback.instantiate(presenterType, Blueprint.TypeAllocation.allocateToType(Presenter.class, presenterType,
					PhasedBeanProcessor.of(postProcessor, Phase.POST_CONSTRUCT)));
		}
	}

	private TemporalActiveComponentRegistry setupUI() {
		TemporalActiveComponentRegistry reg = new TemporalActiveComponentRegistry();
		this.root = null;
		try {
			root = setupUI(reg);
		} catch (Exception e) {
			throw new Http500InternalServerErrorException(
					"Unable to initialize view " + getClass().getSimpleName() + ".", e);
		}
		reg.canRegister = false;
		if (root == null) {
			throw new Http901IllegalArgumentException("The returned ui component representing the view "
					+ getClass().getSimpleName() + " was null, which is not allowed.");
		}
		return reg;
	}

	@Override
	protected final Div initContent() {
		if (this.root == null) {
			throw new Http903NotImplementedException(
					"The composition root of an " + View.class.getSimpleName()
							+ " is build during its injection; however, this has not been completed yet.");
		}
		return new Div(this.root);
	}

	Component setupUI(TemporalActiveComponentRegistry reg) throws Exception {
		return buildUI(reg);
	}

	/**
	 * Builds this {@link View}'s UI and return it.
	 * <P>
	 * Is called automatically once after the view's injection.
	 * <P>
	 * Active components that are instantiated during the build can be registered to
	 * the given {@link TemporalActiveComponentRegistry}; they are then available to
	 * listen to by the view's {@link Presenter}'s @{@link Listen} annotated
	 * {@link Method}s.
	 * 
	 * @param reg The {@link TemporalActiveComponentRegistry} the view may register
	 *            its active components to; may <b>not</b> be null.
	 * @return The component containing the UI that represents this view; never null
	 * @throws Exception For convenience, this method may throw any
	 *                   {@link Exception} it desires that can occur during its
	 *                   build.
	 */
	protected abstract Component buildUI(TemporalActiveComponentRegistry reg) throws Exception;
}
