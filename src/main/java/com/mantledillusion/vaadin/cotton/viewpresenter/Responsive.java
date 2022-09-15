package com.mantledillusion.vaadin.cotton.viewpresenter;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.vaadin.cotton.event.responsive.BeforeResponsiveRefreshEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.BrowserDetails;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for any @{@link Route}d {@link Component}s that can react to the client browser's environment by
 * injecting different, more fitting {@link Component} of an @{@link ScreenClass} instead.
 */
@Retention(RUNTIME)
@Target(TYPE)
@PreConstruct(ResponsiveValidator.class)
public @interface Responsive {

    /**
     * Determines a hint of whether a client browser's characteristic has to match the class or can be ignored.
     */
    enum MatchType {

        /**
         * The characteristic has to be true in order for the class to match.
         */
        TRUE,

        /**
         * The characteristic has to be false in order for the class to match.
         */
        FALSE,

        /**
         * The characteristic is ignored, the class matches either way.
         */
        UNDETERMINED
    }

    /**
     * Defines an @{@link Responsive.DeviceClass} to a @{@link Route}d {@link Component} to inject if the requesting client
     * browser's environment matches this @{@link Responsive.DeviceClass}'s configuration.
     * <p>
     * The default configuration is set in a way that single fields of the @{@link Responsive.DeviceClass} can be set individually
     * while ignoring the others. As a result, if no configuration is done at all, the @{@link Responsive.DeviceClass} matches
     * every possible client browser environment there can be.
     * <p>
     * Since a {@link DeviceClass} class cannot change during browsing, this class only has a @{@link Responsive}
     * effect at the moment when a view is visited.
     */
    @Retention(RUNTIME)
    @Target(TYPE)
    @interface DeviceClass {

        /**
         * Determines whether the client has to be {@link BrowserDetails#isAndroid()} for the @{@link DeviceClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        MatchType isAndroid() default MatchType.UNDETERMINED;

        /**
         * Determines whether the client has to be {@link BrowserDetails#isChromeOS()} for the @{@link DeviceClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        MatchType isChromeOS() default MatchType.UNDETERMINED;

        /**
         * Determines whether the client has to be {@link BrowserDetails#isIOS()} for the @{@link DeviceClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @deprecated see {@link BrowserDetails#isIOS()}
         * @return The hint, never null
         */
        @Deprecated
        MatchType isIOS() default MatchType.UNDETERMINED;

        /**
         * Determines whether the client has to be {@link BrowserDetails#isIPhone()} for the @{@link DeviceClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        MatchType isIPhone() default MatchType.UNDETERMINED;

        /**
         * Determines whether the client has to be {@link BrowserDetails#isIPad()} for the @{@link DeviceClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @deprecated see {@link BrowserDetails#isIPad()}
         * @return The hint, never null
         */
        @Deprecated
        MatchType isIPad() default MatchType.UNDETERMINED;

        /**
         * Determines whether the client has to be {@link BrowserDetails#isMacOSX()} for the @{@link DeviceClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        MatchType isMacOSX() default MatchType.UNDETERMINED;

        /**
         * Determines whether the client has to be {@link BrowserDetails#isLinux()} for the @{@link DeviceClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        MatchType isLinux() default MatchType.UNDETERMINED;

        /**
         * Determines whether the client has to be {@link BrowserDetails#isWindows()} for the @{@link DeviceClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        MatchType isWindows() default MatchType.UNDETERMINED;

        /**
         * Determines whether the client has to be {@link BrowserDetails#isWindowsPhone()} for the @{@link DeviceClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        MatchType isWindowsPhone() default MatchType.UNDETERMINED;

        /**
         * Determines whether the {@link DeviceClass}' hints are and-conjoined, which means that all have to match in
         * order for the @{@link DeviceClass} to match.
         * <p>
         * The default is <code>false</code>.
         *
         * @return True if the hints are and-conjoined, false otherwise
         */
        boolean andConjoined() default false;
    }

    /**
     * Defines an @{@link ScreenClass} to a @{@link Route}d {@link Component} to inject if the requesting client
     * browser's environment matches this @{@link ScreenClass}'s configuration.
     * <p>
     * The default configuration is set in a way that single fields of the @{@link ScreenClass} can be set individually
     * while ignoring the others. As a result, if no configuration is done at all, the @{@link ScreenClass} matches
     * every possible client browser environment there can be.
     * <p>
     * Since a {@link ScreenClass} class can change during browsing, this class is @{@link Responsive} for screen
     * size changes.
     */
    @Retention(RUNTIME)
    @Target(TYPE)
    @interface ScreenClass {

        /**
         * Defines modes of how to evaluate a client browser's screen size.
         */
        enum ScreenMode {

            /**
             * Evaluate the screen by absolute pixels.
             * <p>
             * For the @{@link ScreenClass} to match, the browser's resolution has to be in the fixed bounds
             * of @{@link ScreenClass#fromX()} | @{@link ScreenClass#toX()}
             * and @{@link ScreenClass#fromY()} | @{@link ScreenClass#toY()}.
             */
            ABSOLUTE,

            /**
             * Evaluate the screen by its X/Y ratio.
             * <p>
             * For the @{@link ScreenClass} to match, a ratio is build out of the the browser's resolution which has
             * to be in the ratio @{@link ScreenClass#fromX()} / @{@link ScreenClass#fromY()}
             * and the ratio @{@link ScreenClass#toX()} ()} / @{@link ScreenClass#toY()}.
             */
            RATIO
        }

        /**
         * Determines the mode of how the automatic switch to the @{@link ScreenClass} should be performed.
         */
        enum AdaptionMode {

            /**
             * Prohibit the automatic switch to the {@link ScreenClass}; only the
             * {@link BeforeResponsiveRefreshEvent} will be send.
             */
            PROHIBIT,

            /**
             * Perform the automatic switch to the {@link ScreenClass}, as long as the
             * {@link BeforeResponsiveRefreshEvent} is not declined.
             */
            PERFORM,

            /**
             * Always perform the automatic switch to the {@link ScreenClass}; sending the
             * {@link BeforeResponsiveRefreshEvent} is only done for information purposes.
             */
            ENFORCE;

            public static AdaptionMode combine(AdaptionMode a1, AdaptionMode a2) {
                return a1 == AdaptionMode.ENFORCE ? a1 :
                        (a2 == AdaptionMode.ENFORCE ? a2 :
                                (a1 == AdaptionMode.PROHIBIT ? a1 :
                                        (a2 == AdaptionMode.PROHIBIT ? a2 :
                                                (AdaptionMode.PERFORM))));
            }
        }

        /**
         * The mode to use to evaluate the client browser's screen resolution with.
         * <p>
         * The used mode changes the way the fields {@link #fromX()}, {@link #toX()}, {@link #fromY()} and
         * {@link #toY()} are evaluated when trying to match an @{@link ScreenClass} to a client browser's resolution.
         * <p>
         * The default is {@link ScreenMode#ABSOLUTE}, so with {@link Integer#MIN_VALUE}/{@link Integer#MAX_VALUE} used
         * for the from/to values, the default settings match every screen size there is.
         *
         * @return The mode, never null
         */
        ScreenMode mode() default ScreenMode.ABSOLUTE;

        /**
         * The from value to match the browser screen's width (X axis) with.
         * <p>
         * The default is {@link Integer#MIN_VALUE}.
         *
         * @return The from value, never null
         */
        int fromX() default Integer.MIN_VALUE;

        /**
         * The from value to match the browser screen's height (Y axis) with.
         * <p>
         * The default is {@link Integer#MIN_VALUE}.
         *
         * @return The from value, never null
         */
        int fromY() default Integer.MIN_VALUE;

        /**
         * The to value to match the browser screen's width (X axis) with.
         * <p>
         * The default is {@link Integer#MAX_VALUE}.
         *
         * @return The to value, never null
         */
        int toX() default Integer.MAX_VALUE;

        /**
         * The to value to match the browser screen's height (Y axis) with.
         * <p>
         * The default is {@link Integer#MAX_VALUE}.
         *
         * @return The to value, never null
         */
        int toY() default Integer.MAX_VALUE;

        /**
         * Determines whether the client has to be a touch device in order for the @{@link ScreenClass} to match.
         * <p>
         * The default is {@link MatchType#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        MatchType isTouchDevice() default MatchType.UNDETERMINED;

        /**
         * Determines the mode of how the automatic switch to the @{@link ScreenClass} should be performed.
         * <p>
         * The default is {@link AdaptionMode#PERFORM}.
         *
         * @return The automatic adaption mode, never null
         */
        AdaptionMode automaticAdaptionMode() default AdaptionMode.PERFORM;
    }

    /**
     * Defines the @{@link Component}s for the @{@link Route}d component annotated with @{@link Responsive} to inject
     * depending on the client browser's environment.
     * <p>
     * The defined @{@link Component}'s {@link ScreenClass} and @{@link DeviceClass} configurations should be
     * non-overlapping; there should not be a client browser environment existing for which the configurations of
     * multiple @{@link Component}s are matching. If that happens, the first {@link Component} declared is used.
     *
     * @return The @{@link Component}s for the annotated, {@link Route}d {@link Component}, never null, might be empty
     */
    Class<? extends Component>[] value();
}
