package com.mantledillusion.vaadin.cotton.component.css;

/**
 * A {@link CssProperty} that contains enumerable values.
 */
public class CssNamedValueProperty<V extends CssValue> extends AbstractCssProperty {

    CssNamedValueProperty(String stylePropertyName) {
        super(stylePropertyName);
    }

    public CssStyle of(V value) {
        return CssStyle.of(getStylePropertyName(), value.getValue());
    }
}
