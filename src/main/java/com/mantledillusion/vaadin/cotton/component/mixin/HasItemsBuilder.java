package com.mantledillusion.vaadin.cotton.component.mixin;

import java.util.Collection;
import java.util.stream.Stream;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.HasItems;

/**
 * {@link ComponentBuilder} for {@link HasItems} implementing
 * {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasItems}.
 * @param <E>
 *            The element type of the {@link HasItems}.
 * @param <B>
 *            The final implementation type of {@link HasItemsBuilder}.
 */
public interface HasItemsBuilder<C extends HasItems<E>, E, B extends HasItemsBuilder<C, E, B>>
		extends ComponentBuilder<C, B> {

	/**
	 * Builder method, configures a {@link Collection} of elements to set.
	 * 
	 * @see HasItems#setItems(Collection)
	 * @param elements
	 *            The element {@link Collection} to set; might <b>not</b> be null.
	 * @return this
	 */
	default B setItems(Collection<E> elements) {
		return configure(hasItems -> hasItems.setItems(elements));
	}

	/**
	 * Builder method, configures an array of elements to set.
	 * 
	 * @see HasItems#setItems(Object...)
	 * @param elements
	 *            The element array to set.
	 * @return this
	 */
	default B setItems(@SuppressWarnings("unchecked") E... elements) {
		return configure(hasItems -> hasItems.setItems(elements));
	}

	/**
	 * Builder method, configures a S of elements to set.
	 * 
	 * @see HasItems#setItems(Stream)
	 * @param elementStream
	 *            The element stream to set; might <b>not</b> be null.
	 * @return this
	 */
	default B setItems(Stream<E> elementStream) {
		if (elementStream == null) {
			throw new Http901IllegalArgumentException("Cannot set a null element stream");
		}
		return configure(hasItems -> hasItems.setItems(elementStream));
	}
}
