package com.mantledillusion.vaadin.cotton.viewpresenter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.PhasedBeanProcessor;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.BeanProcessor;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.vaadin.cotton.exception.http500.Http500InternalServerErrorException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http902IllegalStateException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http904IllegalAnnotationUseException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentUtil;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * Basic interface for a view that contains active {@link Component}s a presenter hooked using @{@link Presented} on
 * the {@link Presentable} implementation might @{@link Listen} to.
 */
@PostInject(Presentable.PresentableProcessor.class)
public interface Presentable {

	final class PresentedValidator implements AnnotationProcessor<Presented, Class<?>> {

		@Construct
		private PresentedValidator() {}

		@Override
		public void process(Phase phase, Object bean, Presented annotationInstance, Class<?> annotatedElement,
							Injector.TemporalInjectorCallback callback) {
			if (!Presentable.class.isAssignableFrom(annotatedElement)) {
				throw new Http904IllegalAnnotationUseException("The @" + Presented.class.getSimpleName()
						+ " annotation can only be used on " + Presentable.class.getSimpleName()
						+ " implementations; the type '" + annotatedElement.getSimpleName() + "' however is not.");
			}
		}
	}

	final class PresentableProcessor implements BeanProcessor<Presentable> {

		@Construct
		private PresentableProcessor() {}

		@Override
		public void process(Phase phase, Presentable bean, Injector.TemporalInjectorCallback callback) throws Exception {
			TemporalActiveComponentRegistry reg = new TemporalActiveComponentRegistry();
			try {
				bean.registerActiveComponents(reg);
			} catch (Exception e) {
				throw new Http500InternalServerErrorException(
						"Unable to register active components of " + bean.getClass().getSimpleName(), e);
			}
			reg.canRegister = false;

			if (bean.getClass().isAnnotationPresent(Presented.class)) {
				instantiatePresenter(bean, bean.getClass().getAnnotation(Presented.class), reg, callback);
			}
		}

		private <V extends Presentable, T> void instantiatePresenter(V view, Presented annotationInstance,
																	 TemporalActiveComponentRegistry reg,
																	 Injector.TemporalInjectorCallback callback) {
			@SuppressWarnings("unchecked")
			Class<T> presenterType = (Class<T>) annotationInstance.value();

			BeanProcessor<T> postProcessor = (phase, presenter, injectorCallback) -> {
				for (Method method : MethodUtils.getMethodsListWithAnnotation(presenter.getClass(), Listen.class,
						true, true)) {
					// COMPONENT EVENT METHODS
					if (method.isAnnotationPresent(Listen.class)) {
						if (!method.isAccessible()) {
							try {
								method.setAccessible(true);
							} catch (SecurityException e) {
								throw new Http904IllegalAnnotationUseException(
										"Unable to gain access to the method '" + method.getName() + "' of the type "
												+ presenter.getClass().getSimpleName() + ".", e);
							}
						}

						Listen annotation = method.getAnnotation(Listen.class);

						List<Class<? extends ComponentEvent<?>>> eventTypes = Arrays.asList(annotation.extensions());
						if (eventTypes.size() == 0) {
							eventTypes = Arrays.asList((Class<? extends ComponentEvent<?>>) method.getParameterTypes()[0]);
						}

						for (Class<? extends ComponentEvent<?>> eventType: eventTypes) {
							reg.addListener(annotation.value(), eventType, presenter, method);
						}
					}
				}
			};

			Object presenter = callback.instantiate(presenterType, Blueprint.TypeAllocation.allocateToType(presenterType, presenterType,
					PhasedBeanProcessor.of(postProcessor, Phase.POST_CONSTRUCT)));

			if (presenter instanceof AbstractPresenter) {
				((AbstractPresenter<V>) presenter).setView(view);
			}
		}
	}

	/**
	 * Temporarily active registry for {@link Component}s on an {@link Presentable} that are active, which means they fire
	 * events that the {@link Presentable}s presenter has to react on.
	 * <P>
	 * May only be used during the initialization of the {@link Presentable} it is given to.
	 */
	final class TemporalActiveComponentRegistry {

		private final Map<Component, String> activeComponents = new IdentityHashMap<>();
		private boolean canRegister = true;

		private TemporalActiveComponentRegistry() {}

		/**
		 * Registers the given {@link Component}, which will make the component's events @{@link Listen}able to for presenter methods.
		 *
		 * @param <T> The type of the {@link Component} to register.
		 * @param component The component to register; might <b>not</b> be null or has a null component id.
		 * @return The given component, for inline building
		 */
		public <T extends Component> T register(T component) {
			return register(component, null);
		}

		/**
		 * Registers the given {@link Component}, which will make the component's events @{@link Listen}able to for presenter methods.
		 * 
		 * @param <T> The type of the {@link Component} to register.
		 * @param component The component to register; might <b>not</b> be null or has a null component id.
		 * @param id The component id to set; might be null.
		 * @return The given component, for inline building
		 */
		public <T extends Component> T register(T component, String id) {
			if (!canRegister) {
				throw new Http902IllegalStateException(
						"The component registry may only be used during the initialization of the view it is given to, "
								+ "as components registered later would not be linked to the view's contolling subscriber anymore.");
			} else if (component == null) {
				throw new Http901IllegalArgumentException("Cannot register a null component.");
			} else if (id != null) {
				component.setId(id);
			}

			if (!component.getId().isPresent()) {
				throw new Http901IllegalArgumentException("Cannot register a component without an id.");
			} else {
				this.activeComponents.put(component, component.getId().get());
			}
			return component;
		}

		private <T extends ComponentEvent<?>> void addListener(String componentIdMatcher, Class<T> eventType, Object presenter, Method m) {
			this.activeComponents.entrySet().parallelStream()
					.filter(entry -> entry.getValue().matches(componentIdMatcher))
					.forEach(entry -> routeEvent(entry.getKey(), eventType, m, presenter));
		}

		private <T extends ComponentEvent<?>> void routeEvent(Component c, Class<T> eventType, Method m, Object presenter) {
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

	/**
	 * Allows registering {@link Component}s as active to a {@link TemporalActiveComponentRegistry} so a presenter is
	 * able to @{@link Listen} to its events.
	 * <p>
	 * The default implementation does nothing.
	 *
	 * @param reg The {@link Presentable.TemporalActiveComponentRegistry} the view may register its active components to;
	 *            may <b>not</b> be null.
	 * @throws Exception For convenience, this method may throw any {@link Exception} it desires that can occur during
	 * its registration.
	 */
	default void registerActiveComponents(TemporalActiveComponentRegistry reg) throws Exception {}
}
