package com.mantledillusion.vaadin.cotton.viewpresenter;

import com.mantledillusion.vaadin.cotton.component.builders.DialogBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dialog.Dialog;

import java.lang.reflect.Method;

/**
 * Basic super type for {@link Presentable}s that are a {@link Composite} of different {@link Component}s and should be
 * shown in a {@link Dialog} window.
 */
public abstract class AbstractFrame extends AbstractView {

    private Dialog dialog;

    @Override
    protected final Dialog buildUI(TemporalActiveComponentRegistry reg) throws Exception {
        DialogBuilder builder = DialogBuilder.create();
        Component content = buildUI(builder, reg);
        this.dialog = builder.build();
        this.dialog.add(content);
        return this.dialog;
    }

    /**
     * Builds this {@link Presentable}'s UI and return it.
     * <P>
     * Is called automatically once after the view's injection.
     * <P>
     * Active components that are instantiated during the build can be registered to the given
     * {@link Presentable.TemporalActiveComponentRegistry}; they are then available to listen to by the view's
     * {@link AbstractPresenter}'s @{@link Listen} annotated {@link Method}s.
     *
     * @param builder The builder the {@link Dialog} containing the returned ui will be build with; might <b>not</b>
     *                be null.
     * @param reg The {@link Presentable.TemporalActiveComponentRegistry} the view may register its active components
     *            to; may <b>not</b> be null.
     * @return The component containing the UI that represents this view; never null
     * @throws Exception For convenience, this method may throw any {@link Exception} it desires that can occur
     * during its build.
     */
    protected abstract Component buildUI(DialogBuilder builder, TemporalActiveComponentRegistry reg) throws Exception;

    /**
     * Shows this frame.
     *
     * @see Dialog#open()
     */
    public void show() {
        this.dialog.open();
    }

    /**
     * Returns whether this frame is currently showing.
     *
     * @see Dialog#isOpened()
     * @return True if the frame is showing, false otherwise.
     */
    public boolean isShowing() {
        return this.dialog.isOpened();
    }

    /**
     * Hides this frame.
     *
     * @see Dialog#close()
     */
    public void hide() {
        this.dialog.close();
    }
}