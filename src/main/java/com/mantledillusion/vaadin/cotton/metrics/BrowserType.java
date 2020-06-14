package com.mantledillusion.vaadin.cotton.metrics;

public enum BrowserType {

    CHROME,
    EDGE,
    FIREFOX,
    IE,
    OPERA,
    SAFARI,

    UNKNOWN;

    public static BrowserType of(boolean isChrome, boolean isEdge, boolean isFirefox, boolean isInternetExplorer,
                                 boolean isOpera, boolean isSafari) {
        if (isChrome) {
            return CHROME;
        } else if (isEdge) {
            return EDGE;
        } else if (isFirefox) {
            return FIREFOX;
        } else if (isInternetExplorer) {
            return IE;
        } else if (isOpera) {
            return OPERA;
        } else if (isSafari) {
            return SAFARI;
        } else {
            return UNKNOWN;
        }
    }
}