package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.mantledillusion.vaadin.metrics.MetricsConsumer;
import com.mantledillusion.vaadin.metrics.MetricsPredicate;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.*;

public final class CottonEnvironment {

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

    public static List<Blueprint.SingletonAllocation> forLocalization(String baseName, String fileExtension, Charset charset, Locale locale,
                                                                 Locale... locales) {
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
}