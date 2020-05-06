package com.mantledillusion.vaadin.cotton.component.css;

import com.helger.css.ECSSUnit;

/**
 * A {@link CssProperty} that contains a value of some sizing unit.
 */
public class CssSizeProperty extends AbstractCssProperty {

    CssSizeProperty(String stylePropertyName) {
        super(stylePropertyName);
    }

    public CssStyle of(double value, ECSSUnit unit) {
        return CssStyle.of(getStylePropertyName(), unit.format(value));
    }
}
