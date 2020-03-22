package com.mantledillusion.vaadin.cotton.viewpresenter;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.vaadin.cotton.exception.http900.Http904IllegalAnnotationUseException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;

import java.lang.annotation.Annotation;

final class RoutedValidator implements AnnotationProcessor<Annotation, Class<?>> {

    @Construct
    private RoutedValidator() {}

    @Override
    public void process(Phase phase, Object bean, Annotation annotationInstance, Class<?> annotatedElement, Injector.TemporalInjectorCallback callback) throws Exception {
        if (!Component.class.isAssignableFrom(annotatedElement)) {
            throw new Http904IllegalAnnotationUseException("The class " + annotatedElement.getSimpleName() +
                    " has to be an extension to the class " + Component.class + " in order to be annotated with @" +
                    annotationInstance.getClass().getSimpleName());
        } else if (!annotatedElement.isAnnotationPresent(Route.class)) {
            throw new Http904IllegalAnnotationUseException("The class " + annotatedElement.getSimpleName() +
                    " has to be annotated with @" + Route.class + " in order to be annotated with @" + annotationInstance.getClass().getSimpleName());
        }
    }
}
