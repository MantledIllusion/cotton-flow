package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.vaadin.cotton.component.builders.LabelBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.page.Push;

import com.mantledillusion.injection.hura.core.annotation.instruction.Define;
import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.metrics.trail.MetricsConsumer;
import com.mantledillusion.metrics.trail.MetricsPredicate;
import com.mantledillusion.metrics.trail.MetricsTrailConsumer;
import com.mantledillusion.metrics.trail.api.Metric;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive;
import com.mantledillusion.vaadin.cotton.viewpresenter.Restricted;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Offers static methods for @{@link Define}ing {@link Blueprint.Allocation}s in the application's
 * environment {@link Blueprint}.
 */
public final class CottonEnvironment {

    private CottonEnvironment() {}

    // #################################################################################################################
    // ############################################### APPLICATION #####################################################
    // #################################################################################################################

    static final String PKEY_APPLICATION_BASE_PACKAGE = "cotton.application.basePackage";
    static final String PKEY_AUTOMATIC_ROUTE_DISCOVERY = "cotton.application.automaticRouteDiscovery";

    /**
     * Builds a {@link Blueprint.PropertyAllocation} that can @{@link Define} the application's base package. By
     * default, this package is the one the application's environment {@link Blueprint} is resided in.
     * <p>
     * Cotton will use this package for the automatic @{@link com.vaadin.flow.router.Route} discovery.
     *
     * @param basePackage The base package to set; might <b>not</b> be null.
     * @return The {@link Blueprint.Allocation} for the application's environment {@link Blueprint}, never null
     */
    public static Blueprint.PropertyAllocation forApplicationBasePackage(Package basePackage) {
        if (basePackage == null) {
            throw new Http901IllegalArgumentException(
                    "Cannot set the application's base package to null.");
        }
        return Blueprint.PropertyAllocation.of(PKEY_APPLICATION_BASE_PACKAGE, basePackage.getName());
    }

    /**
     * Builds a {@link Blueprint.PropertyAllocation} that can @{@link Define} whether Cotton should automatically
     * define @{@link com.vaadin.flow.router.Route}d {@link com.vaadin.flow.component.Component}s found
     * in {@link Package}s in and underneath the application's base package.
     * <p>
     * By default, this option is enabled.
     *
     * @param discoverRoutesAutomatically True to enable automatic route defining, false otherwise.
     * @return The {@link Blueprint.Allocation} for the application's environment {@link Blueprint}, never null
     */
    public static Blueprint.PropertyAllocation forAutomaticRouteDiscovery(boolean discoverRoutesAutomatically) {
        return Blueprint.PropertyAllocation.of(PKEY_AUTOMATIC_ROUTE_DISCOVERY, Boolean.toString(discoverRoutesAutomatically));
    }

    // #################################################################################################################
    // ################################################## LOGIN ########################################################
    // #################################################################################################################

    static final String SID_LOGIN_PROVIDER = "_loginProvider";

    /**
     * Builds a {@link Blueprint.PropertyAllocation} that can @{@link Define} a {@link LoginProvider} which is
     * triggered for example when a @{@link Restricted} @{@link com.vaadin.flow.router.Route} is visited.
     *
     * @param loginProvider The {@link LoginProvider} to register; might <b>not</b> be null.
     * @return The {@link Blueprint.Allocation} for the application's environment {@link Blueprint}, never null
     */
    public static Blueprint.SingletonAllocation forLoginProvider(LoginProvider loginProvider) {
        if (loginProvider == null) {
            throw new Http901IllegalArgumentException(
                    "Cannot set the application's login provider to a null instance.");
        }
        return Blueprint.SingletonAllocation.allocateToInstance(SID_LOGIN_PROVIDER, loginProvider);
    }

    // #################################################################################################################
    // ############################################## LOCALIZATION #####################################################
    // #################################################################################################################

    static final String PKEY_DEFAULT_LOCALE = "cotton.localization.defaultLocale";

    static final class LocalizationRegistration {

        final Locale locale;
        final Set<String> bundleKeys;
        final ResourceBundle bundle;

        private LocalizationRegistration(Locale locale, Set<String> bundleKeys, ResourceBundle bundle) {
            this.locale = locale;
            this.bundleKeys = bundleKeys;
            this.bundle = bundle;
        }
    }

    /**
     * Builds a {@link Blueprint.PropertyAllocation} that can @{@link Define} the default {@link Locale} language
     * Cotton uses for localization. By default, that is {@link Locale#ENGLISH}.
     *
     * @param defaultLocale The default {@link Locale} to set; might <b>not</b> be null or have {@link Locale#toLanguageTag()} return null.
     * @return The {@link Blueprint.Allocation} for the application's environment {@link Blueprint}, never null
     */
    public static Blueprint.PropertyAllocation forDefaultLocale(Locale defaultLocale) {
        if (defaultLocale == null || defaultLocale.toLanguageTag() == null) {
            throw new Http901IllegalArgumentException(
                    "Cannot set the application's default locale language to null.");
        }
        return Blueprint.PropertyAllocation.of(PKEY_DEFAULT_LOCALE, defaultLocale.toLanguageTag());
    }

    /**
     * Builds a {@link List} of {@link Blueprint.SingletonAllocation}s that can @{@link Define} i18n localizations
     * from resource files.
     * <p>
     * The resource files are expected to reside in the application's src/main/resouces directory like this:<br>
     * - src/main/resources/foo_en.properties<br>
     * - src/main/resources/foo_de.properties<br>
     * - src/main/resources/bar_en.properties<br>
     * - src/main/resources/bar_de.properties<br>
     * <p>
     * For the above example, this method would have to be called twice:<br>
     * - forLocalization("foo", "properties", Charset.forName("UTF-8"), Locale.ENGLISH, Locale.GERMAN);<br>
     * - forLocalization("bar", "properties", Charset.forName("UTF-8"), Locale.ENGLISH, Locale.GERMAN);<br>
     *
     * @param baseName      The base name of the resource file set; might <b>not</b> be null or empty.
     * @param fileExtension The file extension of the resource file set; might <b>not</b> be null or empty.
     * @param charset       The {@link Charset} the resource file set is encoded in; might <b>not</b> be null.
     * @param locale        The first {@link Locale} language whose localizations are encoded in a single file of the resource file set; might <b>not</b> be null.
     * @param locales       Additional {@link Locale} languages; might be null or contain nulls.
     * @return The {@link Blueprint.Allocation}s for the application's environment {@link Blueprint}, never null
     */
    public static List<Blueprint.SingletonAllocation> forLocalization(String baseName, String fileExtension, Charset charset,
                                                                      Locale locale, Locale... locales) {
        if (StringUtils.isBlank(baseName)) {
            throw new Http901IllegalArgumentException(
                    "Cannot register a localization for a blank base name.");
        } else if (StringUtils.isBlank(fileExtension)) {
            throw new Http901IllegalArgumentException(
                    "Cannot register a localization for a blank file extension.");
        } else if (charset == null) {
            throw new Http901IllegalArgumentException(
                    "Cannot register a localization for a null charset.");
        } else if (locale == null) {
            throw new Http901IllegalArgumentException(
                    "Cannot register a localization for a null first locale.");
        }

        List<Blueprint.SingletonAllocation> registrations = new ArrayList<>();

        Localizer.LocalizationControl control = new Localizer.LocalizationControl(charset, fileExtension);
        Set<Locale> uniqueLocales = new HashSet<>();
        uniqueLocales.add(locale);
        uniqueLocales.addAll(Arrays.asList(locales));
        uniqueLocales.remove(null);

        Set<Locale> addedLocales = new HashSet<>();
        Set<String> expectedBundleKeys = new HashSet<>();
        for (Locale loc : uniqueLocales) {
            if (loc != null) {
                Localizer.checkLocale(loc);

                loc = new Locale(loc.getLanguage(), loc.getCountry());

                ResourceBundle bundle;
                try {
                    bundle = ResourceBundle.getBundle(baseName, loc, control);
                } catch (MissingResourceException e) {
                    throw new Http901IllegalArgumentException(
                            "Unable to find localization class resource '" + baseName + '_' + Localizer.toLang(loc) + '.'
                                    + fileExtension + "' for locale " + loc,
                            e);
                }

                Set<String> bundleKeys = new HashSet<>(Collections.list(bundle.getKeys()));
                if (addedLocales.isEmpty()) {
                    addedLocales.add(loc);
                    expectedBundleKeys.addAll(bundleKeys);
                } else {
                    Set<String> difference = SetUtils.disjunction(expectedBundleKeys, bundleKeys);
                    if (difference.isEmpty()) {
                        addedLocales.add(loc);
                    } else {
                        throw new Http901IllegalArgumentException(
                                "The localization resource '" + baseName + '_' + Localizer.toLang(loc) + '.'
                                        + fileExtension + "' for locale " + loc
                                        + " differs from the resources of the already analyzed locales " + addedLocales
                                        + " regarding the message ids " + difference
                                        + "; on differently localed resources of the same base resource, all message id sets have to be equal.");
                    }
                }

                registrations.add(Blueprint.SingletonAllocation.allocateToInstance(new LocalizationRegistration(loc, bundleKeys, bundle)));
            }
        }

        return registrations;
    }

    // #################################################################################################################
    // ############################################# ERROR HANDLING ####################################################
    // #################################################################################################################

    static final String PKEY_ERROR_HANDLING_SUPPORT_EMAIL_ADDRESS = "cotton.errorHandling.supportEmail.address";
    static final String PKEY_ERROR_HANDLING_SUPPORT_EMAIL_SUBJECT = "cotton.errorHandling.supportEmail.subject";
    static final String DEFAULT_ERROR_HANDLING_SUPPORT_EMAIL_SUBJECT = "Error in Application (TrailId: [trailId])";

    /**
     * Builds a {@link List} of {@link Blueprint.SingletonAllocation}s that can @{@link Define} {@link ErrorRenderer}s
     * for displaying errors as simple messages.
     *
     * @param errorType     The {@link Throwable} type to register for; might <b>not</b> be null.
     * @param errorRenderer The {@link ErrorRenderer} to render with; might <b>not</b> be null.
     * @return The {@link Blueprint.Allocation}s for the application's environment {@link Blueprint}, never null
     */
    public Blueprint.SingletonAllocation forErrorMessageProvider(Class<? extends Throwable> errorType, ErrorRenderer<String> errorRenderer) {
        if (errorType == null) {
            throw new IllegalArgumentException("Cannot register error handling for a null error type");
        } else if (errorRenderer == null) {
            throw new IllegalArgumentException("Cannot register error handling for a null renderer");
        }
        return Blueprint.SingletonAllocation.allocateToInstance(new CottonErrorHandler.CottonErrorContentProvider(errorType,
                (injector, httpCode, t, message) -> LabelBuilder.create().
                            setText(errorRenderer.render(httpCode, t, message)).
                            build()));
    }

    /**
     * Builds a {@link List} of {@link Blueprint.SingletonAllocation}s that can @{@link Define} {@link ErrorRenderer}s
     * for displaying errors in complex views.
     *
     * @param <V> The error view type.
     * @param errorType     The {@link Throwable} type to register for; might <b>not</b> be null.
     * @param errorViewType The {@link Component} to render onto; might <b>not</b> be null.
     * @return The {@link Blueprint.Allocation}s for the application's environment {@link Blueprint}, never null
     */
    public <V extends Component & ErrorRenderer<Void>> Blueprint.SingletonAllocation forErrorView(Class<? extends Throwable> errorType, Class<V> errorViewType) {
        if (errorType == null) {
            throw new IllegalArgumentException("Cannot register error handling for a null error type");
        } else if (errorViewType == null) {
            throw new IllegalArgumentException("Cannot register error handling for a null view type");
        }
        return Blueprint.SingletonAllocation.allocateToInstance(new CottonErrorHandler.CottonErrorContentProvider(errorType,
                (injector, httpCode, t, message) -> {
            V errorView = injector.instantiate(errorViewType);
            errorView.render(httpCode, t, message);
            return errorView;
                }));
    }

    /**
     * Builds a {@link Blueprint.PropertyAllocation} that can @{@link Define} the email address to create a
     * "send a mail" style button with on the error dialog displayed by the default {@link ErrorRenderer}.
     *
     * @param emailAddress The email address to open the user's email client for; might <b>not</b> be null.
     * @return The {@link Blueprint.Allocation} for the application's environment {@link Blueprint}, never null
     */
    public static Blueprint.PropertyAllocation forErrorSupportEmailAddress(String emailAddress) {
        if (emailAddress == null) {
            throw new IllegalArgumentException("Cannot use a null error support email address");
        }
        return Blueprint.PropertyAllocation.of(PKEY_ERROR_HANDLING_SUPPORT_EMAIL_ADDRESS, emailAddress);
    }

    /**
     * Builds a {@link Blueprint.PropertyAllocation} that can @{@link Define} the email subject to use for the
     * "send a mail" style button with on the error dialog displayed by the default {@link ErrorRenderer}.
     * <p>
     * The subject can include the following placeholders:<br>
     * - [trailId]: is replaced with the trail id of the session.<br>
     *
     * @param subject The email address to open the user's email client for; might <b>not</b> be null.
     * @return The {@link Blueprint.Allocation} for the application's environment {@link Blueprint}, never null
     */
    public static Blueprint.PropertyAllocation forErrorSupportEmailSubject(String subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Cannot use a null error support email subject");
        }
        return Blueprint.PropertyAllocation.of(PKEY_ERROR_HANDLING_SUPPORT_EMAIL_SUBJECT, subject);
    }

    // #################################################################################################################
    // ############################################### RESPONSIVE ######################################################
    // #################################################################################################################

    /**
     * Builds a {@link Blueprint.PropertyAllocation} that can @{@link Define} the wait time in milliseconds after the
     * last browser resize event will trigger a responsive adaption.
     * <p>
     * Since client browsers will send multiple resize events when their window is resized by dragging it using a
     * mouse, this wait time is useful when trying to keep responsive adaptions down to a reasonable amount.
     * <p>
     * Note that this function only works if the view annotated with @{@link Responsive} is also annotated
     * with @{@link Push} and async support is enabled for the servlet in the application server.
     * <p>
     * By default, the wait time is {@value CottonServletService#DEFAULT_RESPONSIVE_ADAPTION_WAIT_MS} milliseconds.
     *
     * @param waitMs The wait time in milliseconds, 0 or negative values will cause the responsive trigger not to wait
     *               at all.
     * @return The {@link Blueprint.Allocation} for the application's environment {@link Blueprint}, never null
     */
    public static Blueprint.PropertyAllocation forResponsiveAdaptionWaitMs(int waitMs) {
        return Blueprint.PropertyAllocation.of(CottonServletService.PKEY_RESPONSIVE_ADAPTION_WAIT_MS, String.valueOf(waitMs));
    }

    // #################################################################################################################
    // ################################################# METRICS #######################################################
    // #################################################################################################################

    /**
     * Builds a {@link Blueprint.SingletonAllocation} that can @{@link Define} a {@link MetricsConsumer} for the
     * application's Vaadin metrics.
     *
     * @param consumerId The unique id to add the consumer under, which will be delivered
     *                   to the consumer on each
     *                   {@link MetricsConsumer#consume(String, UUID, Metric)}
     *                   invocation. Allows the same consumer to be registered multiple
     *                   times with differing configurations; might <b>not</b> be null.
     * @param consumer   The consumer to add; might <b>not</b> be null.
     * @param gate       The predicate that needs to
     *                   {@link MetricsPredicate#test(Metric)} true to trigger
     *                   flushing all of a session's accumulated {@link Metric}s;
     *                   might be null.
     * @param filter     The predicate that needs to
     *                   {@link MetricsPredicate#test(Metric)} true to allow an
     *                   about-to-be-flushed event to be delivered to the consumer; might
     *                   be null.
     * @return The {@link Blueprint.Allocation} for the application's environment {@link Blueprint}, never null
     * @see MetricsTrailConsumer#from(String, MetricsConsumer, MetricsPredicate, MetricsPredicate)
     */
    public static Blueprint.SingletonAllocation forMetricsConsumer(String consumerId, MetricsConsumer consumer,
                                                                   MetricsPredicate gate, MetricsPredicate filter) {
        return Blueprint.SingletonAllocation.allocateToInstance(MetricsTrailConsumer.from(consumerId, consumer, gate, filter));
    }
}