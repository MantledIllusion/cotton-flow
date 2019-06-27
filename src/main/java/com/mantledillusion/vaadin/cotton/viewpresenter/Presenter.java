package com.mantledillusion.vaadin.cotton.viewpresenter;

/**
 * Basic interface for a presenter that controls a {@link Presentable} after being hooked using @{@link Presented} on
 * the {@link Presentable} implementation might @{@link Listen} to.
 */
public interface Presenter<V extends Presentable> {

    /**
     * Sets the {@link Presentable} this {@link Presenter} presents. Is called automatically on the correct
     * {@link Presenter} a {@link Presentable} is @{@link Presented} by.
     * <p>
     * The default implementation does nothing.
     *
     * @param view
     */
    default void setView(V view) {}
}
