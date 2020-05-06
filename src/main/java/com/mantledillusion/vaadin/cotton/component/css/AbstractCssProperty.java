package com.mantledillusion.vaadin.cotton.component.css;

abstract class AbstractCssProperty implements CssProperty {

    private final String stylePropertyName;

    AbstractCssProperty(String stylePropertyName) {
        this.stylePropertyName = stylePropertyName;
    }

    @Override
    public String getStylePropertyName() {
        return this.stylePropertyName;
    }
}
