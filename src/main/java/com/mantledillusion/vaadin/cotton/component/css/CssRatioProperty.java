package com.mantledillusion.vaadin.cotton.component.css;

/**
 * A {@link CssProperty} that contains a value of a floating point number.
 */
public class CssRatioProperty extends AbstractCssProperty {

    CssRatioProperty(String stylePropertyName) {
        super(stylePropertyName);
    }

    public CssStyle of(double ratio) {
        return CssStyle.of(getStylePropertyName(), String.valueOf(ratio));
    }
}
