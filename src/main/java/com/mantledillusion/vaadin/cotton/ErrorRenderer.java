package com.mantledillusion.vaadin.cotton;

public interface ErrorRenderer<R> {

    R render(int httpCode, Throwable t, String message);
}