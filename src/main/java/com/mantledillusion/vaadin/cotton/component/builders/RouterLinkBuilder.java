package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.*;

/**
 * {@link ComponentBuilder} for {@link RouterLink}s.
 */
public class RouterLinkBuilder extends AbstractComponentBuilder<RouterLink, RouterLinkBuilder> implements
        HasElementBuilder<RouterLink, RouterLinkBuilder>,
        HasComponentsBuilder<RouterLink, RouterLinkBuilder>,
        HasStyleBuilder<RouterLink, RouterLinkBuilder>,
        HasEnabledBuilder<RouterLink, RouterLinkBuilder>,
        HasTextBuilder<RouterLink, RouterLinkBuilder> {

    private RouterLinkBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static RouterLinkBuilder create() {
        return new RouterLinkBuilder();
    }

    @Override
    protected RouterLink instantiate() {
        return new RouterLink();
    }

    /**
     * Builder method, configures the location to navigate to when the link is clicked.
     *
     * @see RouterLink#setRoute(Class)
     * @param navigationTarget
     *            The view to whose location to navigate to; might <b>not</b> be null.
     * @return this
     */
    public RouterLinkBuilder setRoute(Class<? extends Component> navigationTarget) {
        return configure(routerLink -> routerLink.setRoute(navigationTarget));
    }

    /**
     * Builder method, configures the location to navigate to when the link is clicked.
     *
     * @see RouterLink#setRoute(Class, Object)
     * @param navigationTarget
     *            The view to whose location to navigate to; might <b>not</b> be null.
     * @param parameter
     *            The view to whose location to navigate to; might be null.
     * @return this
     */
    public <T, C extends Component & HasUrlParameter<T>> RouterLinkBuilder setRoute(Class<? extends C> navigationTarget, T parameter) {
        return configure(routerLink -> routerLink.setRoute(navigationTarget, parameter));
    }

    /**
     * Builder method, configures the query parameters of location to navigate to when the link is clicked.
     *
     * @see RouterLink#setQueryParameters(QueryParameters)
     * @param queryParameters
     *            The query parameters to set; might be null.
     * @return this
     */
    public RouterLinkBuilder setQueryParameters(QueryParameters queryParameters) {
        return configure(routerLink -> routerLink.setQueryParameters(queryParameters));
    }

    /**
     * Builder method, configures the highlight action to perform when the highlight condition is met.
     *
     * @see RouterLink#setHighlightAction(HighlightAction)
     * @param highlightAction
     *            The highlight action to perform; might <b>not</b> be null.
     * @return this
     */
    public RouterLinkBuilder setHighlightAction(HighlightAction<RouterLink> highlightAction) {
        return configure(routerLink -> routerLink.setHighlightAction(highlightAction));
    }

    /**
     * Builder method, configures the highlight condition determining if the highlight action should be performed after
     * an {@link AfterNavigationEvent}.
     *
     * @see RouterLink#setHighlightCondition(HighlightCondition)
     * @param highlightCondition
     *            The highlight condition to evaluate; might <b>not</b> be null.
     * @return this
     */
    public RouterLinkBuilder setHighlightCondition(HighlightCondition<RouterLink> highlightCondition) {
        return configure(routerLink -> routerLink.setHighlightCondition(highlightCondition));
    }
}
