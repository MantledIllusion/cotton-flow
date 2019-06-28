package com.mantledillusion.vaadin.cotton.viewpresenter;

/**
 * Basic super type for a {@link Presenter} that controls an {@link Presentable}.
 *
 * @param <V>
 *            The type of {@link Presentable} this {@link AbstractPresenter} can control.
 */
public abstract class AbstractPresenter<V extends Presentable> implements Presenter<V> {

	private V view;

	public final V getView() {
		return view;
	}

	@Override
	public void setView(V view) {
		this.view = view;
	}
}
