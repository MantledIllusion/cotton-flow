package com.mantledillusion.vaadin.cotton.component.mixin;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;

/**
 * {@link ComponentBuilder} for {@link HasSize} implementing {@link Component}s.
 *
 * @param <C>
 *            The {@link Component} type implementing {@link HasSize}.
 * @param <B>
 *            The final implementation type of {@link HasSizeBuilder}.
 */
public interface HasSizeBuilder<C extends HasSize, B extends HasSizeBuilder<C, B>> extends ComponentBuilder<C, B> {
	
	public static final String CSS_PX = "px";
	public static final String CSS_PCT = "%";

	/**
	 * Builder method, configures the {@link Component}'s width to be set undefined.
	 * 
	 * @see HasSize#setWidth(String)
	 * @return this
	 */
	default B setWidthUndefined() {
		return configure(hasSize -> hasSize.setWidth(null));
	}

	/**
	 * Builder method, configures the {@link Component}'s height to be set
	 * undefined.
	 * 
	 * @see HasSize#setHeight(String)
	 * @return this
	 */
	default B setHeightUndefined() {
		return configure(hasSize -> hasSize.setHeight(null));
	}

	/**
	 * Builder method, configures the {@link Component}'s width to be set to an
	 * exact pixel value.
	 * 
	 * @see HasSize#setWidth(String)
	 * @param px
	 *            The pixel width to set
	 * @return this
	 */
	default B setExactWidth(int px) {
		return configure(hasSize -> hasSize.setWidth(px + CSS_PX));
	}

	/**
	 * Builder method, configures the {@link Component}'s height to be set to an
	 * exact pixel value.
	 * 
	 * @see HasSize#setHeight(String)
	 * @param px
	 *            The pixel height to set
	 * @return this
	 */
	default B setExactHeight(int px) {
		return configure(hasSize -> hasSize.setHeight(px + CSS_PX));
	}

	/**
	 * Builder method, configures the {@link Component}'s width to be set to a
	 * percental share of its parent's width.
	 * 
	 * @see HasSize#setWidth(String)
	 * @param pct
	 *            The percental width to set
	 * @return this
	 */
	default B setPercentalWidth(int pct) {
		return configure(hasSize -> hasSize.setWidth(pct + CSS_PCT));
	}

	/**
	 * Builder method, configures the {@link Component}'s height to be set to a
	 * percental share of its parent's height.
	 * 
	 * @see HasSize#setHeight(String)
	 * @param pct
	 *            The percental height to set
	 * @return this
	 */
	default B setPercentalheight(int pct) {
		return configure(hasSize -> hasSize.setHeight(pct + CSS_PCT));
	}

	/**
	 * Builder method, configures the {@link Component} to have an undefined width
	 * and height.
	 * 
	 * @see HasSize#setSizeUndefined()
	 * @return this
	 */
	default B setSizeUndefined() {
		return configure(hasSize -> hasSize.setSizeUndefined());
	}

	/**
	 * Builder method, configures the {@link Component} to have a 100% percental
	 * width and height.
	 * 
	 * @see HasSize#setSizeFull()
	 * @return this
	 */
	default B setSizeFull() {
		return configure(hasSize -> hasSize.setSizeFull());
	}
}