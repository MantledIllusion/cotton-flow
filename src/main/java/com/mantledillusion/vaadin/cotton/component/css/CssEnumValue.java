package com.mantledillusion.vaadin.cotton.component.css;

public interface CssEnumValue extends CssValue {

    String name();

    @Override
    default String getValue() {
        return name().toLowerCase().replace('_', '-');
    }
}
