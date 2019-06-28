package com.mantledillusion.vaadin.cotton.viewpresenter;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.vaadin.cotton.exception.http900.Http904IllegalAnnotationUseException;
import com.vaadin.flow.component.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Basic interface for a {@link Presenter} that controls a {@link Presentable} after being hooked using @{@link Presented} on
 * the {@link Presentable} implementation might @{@link Listen} to.
 * <p>
 * Instances of implementations of {@link Presenter} will be instantiated automatically during injection for every
 * {@link Presentable} implementation that requires controlling by an @{@link Presented} annotation on that view's {@link Class}.
 * <P>
 * The {@link Presenter} will automatically be connected to the {@link Presentable} it belongs to, which will be passed
 * to {@link #setView(Presentable)}.
 * <P>
 * All {@link Method}s of {@link Presenter} implementations that are annotated with @{@link Listen} will receive
 * events of specifiable types from {@link Component}s on the presented view that have been registered as active
 * component to the {@link Presentable.TemporalActiveComponentRegistry}.
 *
 * @param <V>
 *            The type of {@link Presentable} this {@link Presenter} can control.
 */
public interface Presenter<V extends Presentable> {

    class ListenValidator implements AnnotationProcessor<Listen, Method> {

        @Construct
        private ListenValidator() {}

        @Override
        public void process(Phase phase, Object bean, Listen annotationInstance, Method method,
                            Injector.TemporalInjectorCallback callback) {
            Parameter[] parameters = method.getParameters();
            if (Modifier.isStatic(method.getModifiers())) {
                throw new Http904IllegalAnnotationUseException("The " + ValidatorUtils.getDescription(method)
                        + " is annotated with @" + Listen.class.getSimpleName() + " but is declared static, which is "
                        + "not allowed.");
            } else if (isValidPattern(annotationInstance.value())) {
                throw new Http904IllegalAnnotationUseException("The " + ValidatorUtils.getDescription(method)
                        + " is annotated with @" + Listen.class.getSimpleName() + " but declares the component id matcher '"
                        + annotationInstance.value() + "', which is not a valid pattern.");
            } else if (parameters.length > 1) {
                throw new Http904IllegalAnnotationUseException("The " + ValidatorUtils.getDescription(method)
                        + " is annotated with @" + Listen.class.getSimpleName() + " but declares " + parameters.length
                        + " parameters; subscribing methods might only receive the event or nothing as argument.");
            } else if (parameters.length == 0) {
                if (annotationInstance.extensions().length == 0) {
                    throw new Http904IllegalAnnotationUseException("The " + ValidatorUtils.getDescription(method)
                            + " is annotated with @" + Listen.class.getSimpleName() + " and declares no parameters, "
                            + "but does also not declare at least one event extension class which is required when not "
                            + "using a parameter.");
                }
            } else if (annotationInstance.extensions().length > 0) {
                Class<?> parameterType = parameters[0].getType();
                for (Class<?> extensionType: annotationInstance.extensions()) {
                    if (!parameterType.isAssignableFrom(extensionType)) {
                        throw new Http904IllegalAnnotationUseException("The " + ValidatorUtils.getDescription(method)
                                + " is annotated with @" + Listen.class.getSimpleName() + " and declares a parameter "
                                + "of the type " + parameterType.getSimpleName() + ", but also declares an extension "
                                + "of the type " + extensionType.getSimpleName() + " which is not assignable.");
                    }
                }
            }
        }
        
        private boolean isValidPattern(String regex) {
            try {
                Pattern.compile(regex);
            } catch (PatternSyntaxException e) {
                return false;
            }
            return true;
        }
    }

    /**
     * Sets the {@link Presentable} this {@link Presenter} presents. Is called automatically on the correct
     * {@link Presenter} a {@link Presentable} is @{@link Presented} by.
     * <p>
     * The default implementation does nothing.
     *
     * @param view The view that is annotated with @{@link Presented} and that this {@link Presenter} instance presents;
     *             might <b>not</b> be null
     */
    default void setView(V view) {}
}
