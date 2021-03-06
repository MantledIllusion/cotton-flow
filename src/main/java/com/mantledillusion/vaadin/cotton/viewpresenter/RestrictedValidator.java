package com.mantledillusion.vaadin.cotton.viewpresenter;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.vaadin.cotton.exception.http900.Http904IllegalAnnotationUseException;
import com.vaadin.flow.component.Component;

final class RestrictedValidator implements AnnotationProcessor<Restricted, Class<?>> {

    @Construct
    private RestrictedValidator() {}

    @Override
    public void process(Phase phase, Object bean, Restricted annotationInstance, Class<?> annotatedElement, Injector.TemporalInjectorCallback callback) throws Exception {
        if (!Component.class.isAssignableFrom(annotatedElement)) {
            throw new Http904IllegalAnnotationUseException("The class " + annotatedElement.getSimpleName() +
                    " has to be an extension to the class " + Component.class + " in order to be annotated with @" +
                    Restricted.class.getSimpleName());
        }
    }
}
