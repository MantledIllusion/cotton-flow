package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;

/**
 * {@link EntityBuilder} for {@link HasStyle} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasStyle}.
 * @param <B>
 *            The final implementation type of {@link HasStyleBuilder}.
 */
public interface HasStyleBuilder<C extends HasStyle, B extends HasStyleBuilder<C, B>> extends EntityBuilder<C, B> {

	/**
	 * Builder method, configures one or more style class names to be added.
	 * 
	 * @see HasStyle#addClassName(String)
	 * @param className
	 *            The first style class name to add; might <b>not</b> be null.
	 * @param classNames
	 *            The second-&gt;nth style class name to add; might be null or
	 *            contain nulls.
	 * @return this
	 */
	default B addStyle(String className, String... classNames) {
		return configure(hasStyle -> {
			hasStyle.addClassName(className);
			hasStyle.addClassNames(classNames);
		});
	}

	/**
	 * Builder method, configures a specific value for a CSS style.
	 *
	 * @see com.vaadin.flow.dom.Style#set(String, String)
	 * @param name
	 *            The first style class name to add; might <b>not</b> be null.
	 * @param value
	 *            The first style class name to add; might be null.
	 * @return this
	 */
	default B setCssStyle(String name, String value) {
		return configure(hasStyle -> hasStyle.getElement().getStyle().set(name, value));
	}
}
