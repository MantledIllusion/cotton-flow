package com.mantledillusion.vaadin.cotton.viewpresenter;

import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http903NotImplementedException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;

import java.lang.reflect.Method;

/**
 * Basic super type for {@link View}s that are a {@link Composite} of different {@link Component}s.
 */
public abstract class AbstractView extends Composite<Div> implements View {

    private static final long serialVersionUID = 1L;

    private Component root;

    @Override
    public final void registerActiveComponents(TemporalActiveComponentRegistry reg) throws Exception {
        this.root = buildUI(reg);
        if (root == null) {
            throw new Http901IllegalArgumentException("The returned ui component representing the view "
                    + getClass().getSimpleName() + " was null, which is not allowed.");
        }
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

    /**
     * Builds this {@link View}'s UI and return it.
     * <P>
     * Is called automatically once after the view's injection.
     * <P>
     * Active components that are instantiated during the build can be registered to
     * the given {@link View.TemporalActiveComponentRegistry}; they are then available to
     * listen to by the view's {@link AbstractPresenter}'s @{@link Listen} annotated
     * {@link Method}s.
     *
     * @param reg The {@link View.TemporalActiveComponentRegistry} the view may register
     *            its active components to; may <b>not</b> be null.
     * @return The component containing the UI that represents this view; never null
     * @throws Exception For convenience, this method may throw any
     *                   {@link Exception} it desires that can occur during its
     *                   build.
     */
    protected abstract Component buildUI(View.TemporalActiveComponentRegistry reg) throws Exception;
}
