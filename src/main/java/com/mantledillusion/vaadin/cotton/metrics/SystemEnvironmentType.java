package com.mantledillusion.vaadin.cotton.metrics;

public enum SystemEnvironmentType {

    ANDROID,
    IPAD,
    IPHONE,
    LINUX,
    MACOSX,
    WINDOWS,
    WINDOWS_PHONE,

    UNKNOWN;

    public static SystemEnvironmentType of(boolean android, boolean iPad, boolean iPhone, boolean linux, boolean macOSX,
                                           boolean windows, boolean windowsPhone) {
        if (android) {
            return ANDROID;
        } else if (iPad) {
            return IPAD;
        } else if (iPhone) {
            return IPHONE;
        } else if (linux) {
            return LINUX;
        } else if (macOSX) {
            return MACOSX;
        } else if (windows) {
            return WINDOWS;
        } else if (windowsPhone) {
            return WINDOWS_PHONE;
        } else {
            return UNKNOWN;
        }
    }
}