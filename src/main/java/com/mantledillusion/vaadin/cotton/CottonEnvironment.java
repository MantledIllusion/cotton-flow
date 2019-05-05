package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.metrics.MetricsConsumer;
import com.mantledillusion.vaadin.metrics.MetricsPredicate;
import com.mantledillusion.vaadin.metrics.api.Metric;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Offers static methods for @{@link com.mantledillusion.injection.hura.core.annotation.instruction.Define}ing
 * {@link Blueprint.Allocation}s in the application's environment {@link Blueprint}.
 */
public final class CottonEnvironment {

    private CottonEnvironment() {}

    // #################################################################################################################
    // ############################################### APPLICATION #####################################################
    // #################################################################################################################

    static final String PKEY_APPLICATION_BASE_PACKAGE = "cotton.application.basePackage";

    /**
     * Builds a {@link Blueprint.PropertyAllocation} that can @{@link com.mantledillusion.injection.hura.core.annotation.instruction.Define}
     * the application's base package. By default, this package is the one the application's environment {@link Blueprint} is resided in.
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
     * Builds a {@link Blueprint.PropertyAllocation} that can @{@link com.mantledillusion.injection.hura.core.annotation.instruction.Define}
     * the default {@link Locale} language Cotton uses for localization. By default, that is {@link Locale#ENGLISH}.
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
     * Builds a {@link List} of {@link Blueprint.SingletonAllocation}s that can @{@link com.mantledillusion.injection.hura.core.annotation.instruction.Define}
     * i18n localizations from resource files.
     * <p>
     * The resource files are expected to reside in the application's src/main/resouces directory like this:<br>
     * - src/main/resouces/foo_en.properties<br>
     * - src/main/resouces/foo_de.properties<br>
     * - src/main/resouces/bar_en.properties<br>
     * - src/main/resouces/bar_de.properties<br>
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
                                "The localization resource '" + baseName + '_' + Localizer.toLang(loc) + '.' + fileExtension
                                        + "' for locale " + loc
                                        + " differs from the resources of the already analyzed locales "
                                        + addedLocales + " regarding the message ids " + difference
                                        + "; on differently localed resources of the same base resource, all message id sets have to be equal.");
                    }
                }

                registrations.add(Blueprint.SingletonAllocation.of(new LocalizationRegistration(loc, bundleKeys, bundle)));
            }
        }

        return registrations;
    }

    // #################################################################################################################
    // ################################################# METRICS #######################################################
    // #################################################################################################################

    static final class MetricsConsumerRegistration {

        final String consumerId;
        final MetricsConsumer consumer;
        final MetricsPredicate gate;
        final MetricsPredicate filter;

        private MetricsConsumerRegistration(String consumerId, MetricsConsumer consumer, MetricsPredicate gate,
                                            MetricsPredicate filter) {
            this.consumerId = consumerId;
            this.consumer = consumer;
            this.gate = gate;
            this.filter = filter;
        }
    }

    /**
     * Builds a {@link Blueprint.SingletonAllocation} that can @{@link com.mantledillusion.injection.hura.core.annotation.instruction.Define}
     * a {@link MetricsConsumer} for the application's Vaadin metrics.
     *
     * @param consumerId The unique id to add the consumer under, which will be delivered
     *                   to the consumer on each
     *                   {@link MetricsConsumer#consume(String, String, Metric)}
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
     * @see com.mantledillusion.vaadin.metrics.MetricsObserverFlow#addConsumer(String, MetricsConsumer, MetricsPredicate, MetricsPredicate)
     */
    public static Blueprint.SingletonAllocation forMetricsConsumer(String consumerId, MetricsConsumer consumer,
                                                                   MetricsPredicate gate, MetricsPredicate filter) {
        if (consumerId == null || consumerId.isEmpty()) {
            throw new Http901IllegalArgumentException("Cannot register a consumer under a null or empty id");
        } else if (consumer == null) {
            throw new Http901IllegalArgumentException("Cannot register a null consumer");
        }
        return Blueprint.SingletonAllocation.of(new MetricsConsumerRegistration(consumerId, consumer, gate, filter));
    }
}