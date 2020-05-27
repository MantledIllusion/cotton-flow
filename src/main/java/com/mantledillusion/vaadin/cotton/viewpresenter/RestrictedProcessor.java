package com.mantledillusion.vaadin.cotton.viewpresenter;

import com.mantledillusion.essentials.expression.Expression;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.vaadin.cotton.WebEnv;
import com.vaadin.flow.component.Component;
import org.apache.commons.lang3.StringUtils;

final class RestrictedProcessor implements AnnotationProcessor<Restricted, Class<? extends Component>> {

    @Construct
    private RestrictedProcessor() {}

    @Override
    public void process(Phase phase, Object bean, Restricted annotationInstance, Class<? extends Component> annotatedElement, Injector.TemporalInjectorCallback callback) throws Exception {
        ((Component) bean).setVisible(StringUtils.isBlank(annotationInstance.value()) ? WebEnv.isLoggedIn() : WebEnv.userHasRights(Expression.parse(annotationInstance.value())));
    }
}
