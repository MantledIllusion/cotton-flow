package com.mantledillusion.vaadin.cotton.component.css;

import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.component.HasElement;

import java.util.Objects;

/**
 * Interface for a tuple of a CSS style property name and a value.
 */
public interface CssStyle extends CssProperty, CssValue {

    /**
     * The CSS "background-color" style property.
     */
    CssColorProperty BACKGROUND_COLOR = new CssColorProperty("background-color");

    /**
     * The CSS "border-color" style property.
     */
    CssColorProperty BORDER_COLOR = new CssColorProperty("border-color");

    /**
     * The CSS "border-top-color" style property.
     */
    CssColorProperty BORDER_TOP_COLOR = new CssColorProperty("border-top-color");

    /**
     * The CSS "border-right-color" style property.
     */
    CssColorProperty BORDER_RIGHT_COLOR = new CssColorProperty("border-right-color");

    /**
     * The CSS "border-bottom-color" style property.
     */
    CssColorProperty BORDER_BOTTOM_COLOR = new CssColorProperty("border-bottom-color");

    /**
     * The CSS "border-left-color" style property.
     */
    CssColorProperty BORDER_LEFT_COLOR = new CssColorProperty("border-left-color");

    /**
     * The CSS "border-style" style property.
     */
    CssNamedValueProperty<CssBorderStyleType> BORDER_STYLE = new CssNamedValueProperty<>("border-style");

    /**
     * The CSS "border-top-style" style property.
     */
    CssNamedValueProperty<CssBorderStyleType> BORDER_TOP_STYLE = new CssNamedValueProperty<>("border-top-style");

    /**
     * The CSS "border-right-style" style property.
     */
    CssNamedValueProperty<CssBorderStyleType> BORDER_RIGHT_STYLE = new CssNamedValueProperty<>("border-right-style");

    /**
     * The CSS "border-bottom-style" style property.
     */
    CssNamedValueProperty<CssBorderStyleType> BORDER_BOTTOM_STYLE = new CssNamedValueProperty<>("border-bottom-style");

    /**
     * The CSS "border-left-style" style property.
     */
    CssNamedValueProperty<CssBorderStyleType> BORDER_LEFT_STYLE = new CssNamedValueProperty<>("border-left-style");

    /**
     * The CSS "border-width" style property.
     */
    CssSizeProperty BORDER_WIDTH = new CssSizeProperty("border-width");

    /**
     * The CSS "border-top-width" style property.
     */
    CssSizeProperty BORDER_TOP_WIDTH = new CssSizeProperty("border-top-width");

    /**
     * The CSS "border-right-width" style property.
     */
    CssSizeProperty BORDER_RIGHT_WIDTH = new CssSizeProperty("border-right-width");

    /**
     * The CSS "border-bottom-width" style property.
     */
    CssSizeProperty BORDER_BOTTOM_WIDTH = new CssSizeProperty("border-bottom-width");

    /**
     * The CSS "border-left-width" style property.
     */
    CssSizeProperty BORDER_LEFT_WIDTH = new CssSizeProperty("border-left-width");

    /**
     * The CSS "color" style property.
     */
    CssColorProperty COLOR = new CssColorProperty("color");

    /**
     * The CSS "cursor" style property.
     */
    CssNamedValueProperty<CssCursor> CURSOR = new CssNamedValueProperty<>("cursor");

    /**
     * The CSS "flex-grow" style property.
     */
    CssRatioProperty FLEX_GROW = new CssRatioProperty("flex-grow");

    /**
     * The CSS "flex-shrink" style property.
     */
    CssRatioProperty FLEX_SHRINK = new CssRatioProperty("flex-shrink");

    /**
     * The CSS "font-size" style property.
     */
    CssSizeProperty FONT_SIZE = new CssSizeProperty("font-size");

    /**
     * The CSS "font-style" style property.
     */
    CssNamedValueProperty<CssFontStyleType> FONT_STYLE = new CssNamedValueProperty<>("font-style");

    /**
     * The CSS "font-weight" style property.
     */
    CssNamedValueProperty<CssFontWeightType> FONT_WEIGHT = new CssNamedValueProperty<>("font-weight");

    /**
     * The CSS "margin-top" style property.
     */
    CssSizeProperty MARGIN_TOP = new CssSizeProperty("margin-top");

    /**
     * The CSS "margin-right" style property.
     */
    CssSizeProperty MARGIN_RIGHT = new CssSizeProperty("margin-right");

    /**
     * The CSS "margin-bottom" style property.
     */
    CssSizeProperty MARGIN_BOTTOM = new CssSizeProperty("margin-bottom");

    /**
     * The CSS "margin-left" style property.
     */
    CssSizeProperty MARGIN_LEFT = new CssSizeProperty("margin-left");

    /**
     * The CSS "margin" style property.
     */
    CssSizeProperty MARGIN = new CssSizeProperty("margin");

    /**
     * The CSS "min-width" style property.
     */
    CssSizeProperty MIN_WIDTH = new CssSizeProperty("min-width");

    /**
     * The CSS "max-width" style property.
     */
    CssSizeProperty MAX_WIDTH = new CssSizeProperty("max-width");

    /**
     * The CSS "min-height" style property.
     */
    CssSizeProperty MIN_HEIGHT = new CssSizeProperty("min-height");

    /**
     * The CSS "max-height" style property.
     */
    CssSizeProperty MAX_HEIGHT = new CssSizeProperty("max-height");

    /**
     * The CSS "overflow-x" style property.
     */
    CssNamedValueProperty<CssOverflowType> OVERFLOW_X = new CssNamedValueProperty<>("overflow-x");

    /**
     * The CSS "overflow-y" style property.
     */
    CssNamedValueProperty<CssOverflowType> OVERFLOW_Y = new CssNamedValueProperty<>("overflow-y");

    /**
     * The CSS "padding-top" style property.
     */
    CssSizeProperty PADDING_TOP = new CssSizeProperty("padding-top");

    /**
     * The CSS "padding-right" style property.
     */
    CssSizeProperty PADDING_RIGHT = new CssSizeProperty("padding-right");

    /**
     * The CSS "padding-bottom" style property.
     */
    CssSizeProperty PADDING_BOTTOM = new CssSizeProperty("padding-bottom");

    /**
     * The CSS "padding-left" style property.
     */
    CssSizeProperty PADDING_LEFT = new CssSizeProperty("padding-left");

    /**
     * The CSS "padding" style property.
     */
    CssSizeProperty PADDING = new CssSizeProperty("padding");

    /**
     * The CSS "width" style property.
     */
    CssSizeProperty WIDTH = new CssSizeProperty("width");

    /**
     * The CSS "height" style property.
     */
    CssSizeProperty HEIGHT = new CssSizeProperty("height");

    /**
     * Applies this style on the given {@link HasElement}.
     *
     * @param hasElement The element to apply on; might <b>not</b> be null.
     */
    default void apply(HasElement hasElement) {
        if (hasElement == null) {
            throw new Http901IllegalArgumentException("Cannot apply style on a null element");
        }
        hasElement.getElement().getStyle().set(this.getStylePropertyName(), this.getValue());
    }

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

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || !(o instanceof CssStyle)) return false;
                CssStyle that = (CssStyle) o;
                return stylePropertyName.equals(that.getStylePropertyName()) && value.equals(that.getValue());
            }

            @Override
            public int hashCode() {
                return Objects.hash(stylePropertyName, value);
            }

            @Override
            public String toString() {
                return stylePropertyName + ": " + value;
            }
        };
    }
}
