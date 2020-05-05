package com.mantledillusion.vaadin.cotton.component.mixin;

import com.helger.css.ECSSUnit;
import com.mantledillusion.vaadin.cotton.component.EntityBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import org.apache.commons.lang3.math.NumberUtils;

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
	 * Interface for a tuple of a CSS style property name and a value.
	 */
	interface CssStyle {

		/**
		 * Returns the name of the style property.
		 *
		 * @return The name, never null
		 */
		String getStylePropertyName();

		/**
		 * Returns the value to the stype property.
		 *
		 * @return The value, might be null
		 */
		String getValue();

		/**
		 * Creates an anonymous {@link CssStyle}.
		 *
		 * @param stylePropertyName The name of the style property; might <b>not</b> be null,
		 * @param value The value to the style property; might be null.
		 * @return A new {@link CssStyle} instance, never null
		 */
		static CssStyle of(String stylePropertyName, String value) {
			return new CssStyle() {

				@Override
				public String getStylePropertyName() {
					return stylePropertyName;
				}

				@Override
				public String getValue() {
					return value;
				}
			};
		}
	}

	/**
	 * CSS Styles regarding overflow.
	 */
	enum CssOverflowStyle {

		/**
		 * The CSS "overflow-x" style property.
		 */
		X("overflow-x"),

		/**
		 * The CSS "overflow-y" style property.
		 */
		Y("overflow-y");

		/**
		 * Type describing all possible values of CSS overflow properties.
		 */
		public enum CssOverflowType {

			AUTO,
			HIDDEN,
			INHERIT,
			INITIAL,
			REVERT,
			SCROLL,
			UNSET,
			VISIBLE
		}

		private final String stylePropertyName;

		CssOverflowStyle(String stylePropertyName) {
			this.stylePropertyName = stylePropertyName;
		}

		public CssStyle of(CssOverflowType type) {
			return CssStyle.of(this.stylePropertyName, type.name().toLowerCase());
		}
	}

	/**
	 * CSS Styles regarding padding.
	 */
	enum CssPaddingStyle {

		/**
		 * The CSS "padding-top" style property.
		 */
		TOP("padding-top"),

		/**
		 * The CSS "padding-right" style property.
		 */
		RIGHT("padding-right"),

		/**
		 * The CSS "padding-bottom" style property.
		 */
		BOTTOM("padding-bottom"),

		/**
		 * The CSS "padding-left" style property.
		 */
		LEFT("padding-left"),

		/**
		 * The CSS "padding" style property.
		 */
		ALL("padding");

		private final String stylePropertyName;

		CssPaddingStyle(String stylePropertyName) {
			this.stylePropertyName = stylePropertyName;
		}

		public CssStyle of(double value, ECSSUnit unit) {
			return CssStyle.of(this.stylePropertyName, unit.format(value));
		}
	}

	/**
	 * CSS Styles regarding margin.
	 */
	enum CssMarginStyle {

		/**
		 * The CSS "margin-top" style property.
		 */
		TOP("margin-top"),

		/**
		 * The CSS "margin-right" style property.
		 */
		RIGHT("margin-right"),

		/**
		 * The CSS "margin-bottom" style property.
		 */
		BOTTOM("margin-bottom"),

		/**
		 * The CSS "margin-left" style property.
		 */
		LEFT("margin-left"),

		/**
		 * The CSS "margin" style property.
		 */
		ALL("margin");

		private final String stylePropertyName;

		CssMarginStyle(String stylePropertyName) {
			this.stylePropertyName = stylePropertyName;
		}

		public CssStyle of(double value, ECSSUnit unit) {
			return CssStyle.of(this.stylePropertyName, unit.format(value));
		}
	}

	/**
	 * CSS Styles regarding coloring.
	 */
	enum CssColorStyle {

		/**
		 * The CSS "color" style property.
		 */
		COLOR("color"),

		/**
		 * The CSS "background-color" style property.
		 */
		BACKGROUND_COLOR("background-color");

		private final String stylePropertyName;

		CssColorStyle(String stylePropertyName) {
			this.stylePropertyName = stylePropertyName;
		}

		public CssStyle ofRGB(int r, int g, int b) {
			return ofRGB(r, g, b, 1);
		}

		public CssStyle ofRGB(int r, int g, int b, double a) {
			if (NumberUtils.min(r, g, b, a) < 0 || NumberUtils.max(r, g, b) > 255 || a > 1) {
				throw new Http901IllegalArgumentException("Cannot create color style with values out of range 0<=RGB<=1 or 0<=A<=1");
			}
			return CssStyle.of(this.stylePropertyName, a == 1 ? "rgb("+r+","+g+","+b+")" : "rgba("+r+","+g+","+b+","+a+")");
		}

		public CssStyle ofHSL(double h, double s, double l) {
			return ofHSL(h, s, l, 1);
		}

		public CssStyle ofHSL(double h, double s, double l, double a) {
			if (NumberUtils.min(h, s, l, a) < 0 || NumberUtils.max(h, s, l, a) > 1) {
				throw new Http901IllegalArgumentException("Cannot create color style with values out of range 0<=HSBL<=1");
			}
			return CssStyle.of(this.stylePropertyName, a == 1 ? "hsl("+h+","+s+","+l+")" : "hsla("+h+","+s+","+l+","+a+")");
		}

		public CssStyle ofHSB(double h, double s, double b) {
			return ofHSB(h, s, b, 1);
		}

		public CssStyle ofHSB(double h, double s, double b, double a) {
			if (NumberUtils.min(h, s, b, a) < 0 || NumberUtils.max(h, s, b, a) > 1) {
				throw new Http901IllegalArgumentException("Cannot create color style with values out of range 0<=HSBA<=1");
			}

			if (s == 0.0D) {
				return ofRGB((int) Math.round(b * 255), (int) Math.round(b * 255), (int) Math.round(b * 255));
			} else {
				double var_h = h * 6.0D;
				if (var_h == 6.0D) {
					var_h = 0.0D;
				}

				double var_i = Math.floor(var_h);
				double var_1 = b * (1.0D - s);
				double var_2 = b * (1.0D - s * (var_h - var_i));
				double var_3 = b * (1.0D - s * (1.0D - (var_h - var_i)));
				double var_r;
				double var_g;
				double var_b;
				if (var_i == 0.0D) {
					var_r = b;
					var_g = var_3;
					var_b = var_1;
				} else if (var_i == 1.0D) {
					var_r = var_2;
					var_g = b;
					var_b = var_1;
				} else if (var_i == 2.0D) {
					var_r = var_1;
					var_g = b;
					var_b = var_3;
				} else if (var_i == 3.0D) {
					var_r = var_1;
					var_g = var_2;
					var_b = b;
				} else if (var_i == 4.0D) {
					var_r = var_3;
					var_g = var_1;
					var_b = b;
				} else {
					var_r = b;
					var_g = var_1;
					var_b = var_2;
				}

				return ofRGB((int) Math.round(var_r * 255), (int) Math.round(var_g * 255), (int) Math.round(var_b * 255), a);
			}
		}
	}

	/**
	 * CSS Styles regarding font size.
	 */
	class CssFontSizeStyle {

		public static CssStyle of(double value, ECSSUnit unit) {
			return CssStyle.of("font-size", unit.format(value));
		}
	}

	/**
	 * CSS Styles regarding font style.
	 */
	enum CssFontStyleStyle implements CssStyle {

		/**
		 * The CSS "font-style" style property "bold".
		 */
		ITALIC("italic"),

		/**
		 * The CSS "font-style" style property "normal".
		 */
		NORMAL("normal");

		private final String value;

		CssFontStyleStyle(String value) {
			this.value = value;
		}

		@Override
		public String getStylePropertyName() {
			return "font-style";
		}

		@Override
		public String getValue() {
			return this.value;
		}
	}

	/**
	 * CSS Styles regarding font weight.
	 */
	enum CssFontWeightStyle implements CssStyle {

		/**
		 * The CSS "font-weight" style property "bold".
		 */
		BOLD("bold"),

		/**
		 * The CSS "font-weight" style property "bolder".
		 */
		BOLDER("bolder"),

		/**
		 * The CSS "font-weight" style property "inherit".
		 */
		INHERIT("inherit"),

		/**
		 * The CSS "font-weight" style property "lighter".
		 */
		LIGHTER("lighter"),

		/**
		 * The CSS "font-weight" style property "normal".
		 */
		NORMAL("normal");

		private final String value;

		CssFontWeightStyle(String value) {
			this.value = value;
		}

		@Override
		public String getStylePropertyName() {
			return "font-weight";
		}

		@Override
		public String getValue() {
			return this.value;
		}
	}

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
	 *            The first style property name to add; might <b>not</b> be null.
	 * @param value
	 *            The first style value to set; might be null.
	 * @return this
	 */
	default B setCssStyle(String name, String value) {
		return configure(hasStyle -> hasStyle.getElement().getStyle().set(name, value));
	}

	/**
	 * Builder method, configures a specific value for a CSS style.
	 *
	 * @see com.vaadin.flow.dom.Style#set(String, String)
	 * @param style
	 *            The style to add; might <b>not</b> be null.
	 * @return this
	 */
	default B setCssStyle(CssStyle style) {
		return configure(hasStyle -> hasStyle.getElement().getStyle().set(style.getStylePropertyName(), style.getValue()));
	}
}
