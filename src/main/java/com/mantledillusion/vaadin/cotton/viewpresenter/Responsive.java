package com.mantledillusion.vaadin.cotton.viewpresenter;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.vaadin.cotton.event.responsive.BeforeResponsiveRefreshEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Component;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for any @{@link Route}d {@link Component}s that can react to the client browser's environment by
 * injecting different, more fitting {@link Component} an an @{@link Alternative} instead.
 */
@Retention(RUNTIME)
@Target(TYPE)
@PreConstruct(RoutedValidator.class)
public @interface Responsive {

    /**
     * Defines an @{@link Alternative} to a @{@link Route}d {@link Component} to inject if the requesting client
     * browser's environment matches this @{@link Alternative}'s configuration.
     * <p>
     * The default configuration is set in a way that single fields of the @{@link Alternative} can be set individually
     * while ignoring the others. As a result, if no configuration is done at all, the @{@link Alternative} matches
     * every possible client browser environment there can be.
     */
    @interface Alternative {

        /**
         * Defines modes of how to evaluate a client browser's screen size.
         */
        enum ScreenMode {

            /**
             * Evaluate the screen by absolute pixels.
             * <p>
             * For the @{@link Alternative} to match, the browser's resolution has to be in the fixed bounds
             * of @{@link Alternative#fromX()} | @{@link Alternative#toX()}
             * and @{@link Alternative#fromY()} | @{@link Alternative#toY()}.
             */
            ABSOLUTE,

            /**
             * Evaluate the screen by its X/Y ratio.
             * <p>
             * For the @{@link Alternative} to match, a ratio is build out of the the browser's resolution which has
             * to be in the ratio @{@link Alternative#fromX()} / @{@link Alternative#fromY()}
             * and the ratio @{@link Alternative#toX()} ()} / @{@link Alternative#toY()}.
             */
            RATIO
        }

        /**
         * Determines a hint of whether a client browser's characteristic has to match the @{@link Alternative} or can
         * be ignored.
         */
        enum DeviceHint {

            /**
             * The characteristic has to be true in order for the @{@link Alternative} to match.
             */
            TRUE,

            /**
             * The characteristic has to be false in order for the @{@link Alternative} to match.
             */
            FALSE,

            /**
             * The characteristic is ignored, the @{@link Alternative} matches either way.
             */
            UNDETERMINED
        }

        /**
         * Determines the mode of how the automatic switch to the @{@link Alternative} should be performed.
         */
        enum AdaptionMode {

            /**
             * Prohibit the automatic switch to the {@link Alternative}; only the
             * {@link BeforeResponsiveRefreshEvent} will be send.
             */
            PROHIBIT,

            /**
             * Perform the automatic switch to the {@link Alternative}, as long as the
             * {@link BeforeResponsiveRefreshEvent} is not declined.
             */
            PERFORM,

            /**
             * Always perform the automatic switch to the {@link Alternative}; sending the
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
         * The alternative {@link Component} to inject instead of the {@link Component} annotated
         * with @{@link Responsive} if the @{@link Alternative}'s configuration matches.
         *
         * @return The alternative {@link Component}, never null
         */
        Class<? extends Component> value();

        /**
         * The mode to use to evaluate the client browser's screen resolution with.
         * <p>
         * The used mode changes the way the fields {@link #fromX()}, {@link #toX()}, {@link #fromY()} and
         * {@link #toY()} are evaluated when trying to match an @{@link Alternative} to a client browser's resolution.
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
         * Determines whether the client has to be a mobile device in order for the @{@link Alternative} to match.
         * <p>
         * The default is {@link DeviceHint#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        DeviceHint isMobileDevice() default DeviceHint.UNDETERMINED;

        /**
         * Determines whether the client has to be a touch device in order for the @{@link Alternative} to match.
         * <p>
         * The default is {@link DeviceHint#UNDETERMINED}.
         *
         * @return The hint, never null
         */
        DeviceHint isTouchDevice() default DeviceHint.UNDETERMINED;

        /**
         * Determines the mode of how the automatic switch to the @{@link Alternative} should be performed.
         * <p>
         * The default is {@link AdaptionMode#PERFORM}.
         *
         * @return The automatic adaption mode, never null
         */
        AdaptionMode automaticAdaptionMode() default AdaptionMode.PERFORM;
    }

    /**
     * Defines the @{@link Alternative}s for the @{@link Route}d component annotated with @{@link Responsive} to inject
     * depending on the client browser's environment.
     * <p>
     * The defined @{@link Alternative}'s configurations have to be non-overlapping; there should not be a client
     * browser environment existing for which the configurations of multiple @{@link Alternative}s are matching. If
     * that happens, it cannot be decided which @{@link Alternative} to inject, so the {@link Component} annotated
     * with @{@link Responsive} is injected as a fallback and a warning is logged.
     *
     * @return The @{@link Alternative}s for the annotated, {@link Route}d {@link Component}, never null, might be empty
     */
    Alternative[] value();
}
