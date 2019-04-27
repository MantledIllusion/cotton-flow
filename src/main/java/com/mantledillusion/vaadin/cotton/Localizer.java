package com.mantledillusion.vaadin.cotton;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mantledillusion.vaadin.cotton.CottonServletService.SessionBean;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinSession;

class Localizer implements SessionBean, I18NProvider {

	private static final long serialVersionUID = 1L;
	
	static final String SID_LOCALIZER = "_localizer";

	private static final Logger LOGGER = LoggerFactory.getLogger(Localizer.class);

	private static final String REGEX_MESSAGE_ID_NAME_SEGMENT = "[^\\.\\s]+";
	private static final String REGEX_TYPICAL_MESSAGE_ID = REGEX_MESSAGE_ID_NAME_SEGMENT + "(\\."
			+ REGEX_MESSAGE_ID_NAME_SEGMENT + ")+";

	// #########################################################################################################################################
	// ################################################################ CONTROL ################################################################
	// #########################################################################################################################################

	private static final class LocalizationControl extends java.util.ResourceBundle.Control {

		private final Charset charset;
		private final String extension;

		private LocalizationControl(Charset charset, String extension) {
			this.charset = charset;
			this.extension = extension;
		}

		@Override
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
				boolean reload) throws IllegalAccessException, InstantiationException, IOException {

			final String bundleName = toBundleName(baseName, locale);
			final String resourceName = toResourceName(bundleName, this.extension);
			ResourceBundle bundle = null;
			InputStream stream = null;
			if (reload) {
				final URL url = loader.getResource(resourceName);
				if (url != null) {
					final URLConnection connection = url.openConnection();
					if (connection != null) {
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				try {
					bundle = new PropertyResourceBundle(new InputStreamReader(stream, this.charset));
				} finally {
					stream.close();
				}
			}
			return bundle;
		}
		
		@Override
		public List<Locale> getCandidateLocales(String baseName, Locale locale) {
			return Arrays.asList(locale);
		}

		@Override
		public String toBundleName(String baseName, Locale locale) {
			return baseName + '_' + toLang(locale);
		}

		@Override
		public Locale getFallbackLocale(String baseName, Locale locale) {
			return null;
		}
	}

	// #########################################################################################################################################
	// ############################################################### RESOURCE ################################################################
	// #########################################################################################################################################

	private interface Evaluateable {

		String evaluate(Map<String, Object> msgParameters);

		Evaluateable collapse();
	}

	private static final class LocalizationResource {

		private final class StaticEvaluateable implements Evaluateable {

			private final String content;

			private StaticEvaluateable(String content) {
				this.content = content;
			}

			@Override
			public String evaluate(Map<String, Object> msgParameters) {
				return replaceOrLocalize(this.content, msgParameters);
			}

			@Override
			public Evaluateable collapse() {
				return this;
			}
		}

		private final class DynamicEvaluateable implements Evaluateable {

			private final List<Evaluateable> content = new ArrayList<>();

			@Override
			public String evaluate(Map<String, Object> msgParameters) {
				StringBuilder sb = new StringBuilder();
				this.content.forEach(evaluateable -> sb.append(evaluateable.evaluate(msgParameters)));
				return replaceOrLocalize(sb.toString(), msgParameters);
			}

			@Override
			public Evaluateable collapse() {
				if (this.content.size() == 1) {
					return this.content.get(0).collapse();
				} else {
					for (int i = 0; i < this.content.size(); i++) {
						this.content.set(i, this.content.get(i).collapse());
					}
					return this;
				}
			}
		}

		private final Locale locale;
		private final Map<String, ResourceBundle> bundles = new HashMap<>();
		private final Map<String, Evaluateable> evaluateables = new HashMap<>();

		private LocalizationResource(Locale locale) {
			this.locale = locale;
		}

		private void addBundle(ResourceBundle bundle, Set<String> bundleKeys) {
			Set<String> intersection = SetUtils.intersection(bundles.keySet(), bundleKeys);
			if (intersection.isEmpty()) {
				MapUtils.populateMap(this.bundles, bundleKeys, key -> key, value -> bundle);
			} else {
				throw new Http901IllegalArgumentException("The resource bundle "
						+ bundle.getBaseBundleName()
						+ " shares the following message ids with other bundles of the same language, which is forbidden: "
						+ Arrays.toString(intersection.stream()
								.map(key -> "'" + key + "' (also in " + this.bundles.get(key).getBaseBundleName() + ")")
								.toArray()));
			}
		}

		private boolean hasLocalization(String msgId) {
			return this.bundles.containsKey(msgId);
		}

		private String renderMessage(String msgId, Map<String, ?> namedMsgParameters, Object... indexedMsgParameters) {
			if (this.bundles.containsKey(msgId)) {
				Map<String, Object> params = new HashMap<>(
						ObjectUtils.defaultIfNull(namedMsgParameters, Collections.emptyMap()));
				indexedMsgParameters = ObjectUtils.defaultIfNull(indexedMsgParameters, ArrayUtils.EMPTY_OBJECT_ARRAY);
				for (int i = 0; i < indexedMsgParameters.length; i++) {
					params.put(String.valueOf(i), indexedMsgParameters[i]);
				}

				if (!this.evaluateables.containsKey(msgId)) {
					this.evaluateables.put(msgId, createEvaluteable(msgId, this.bundles.get(msgId).getString(msgId)));
				}
				return this.evaluateables.get(msgId).evaluate(params);
			} else if (msgId.matches(REGEX_TYPICAL_MESSAGE_ID)) {
				LOGGER.warn("Unable to localize '" + msgId + "' with bundle of language '" + getLang()
						+ "': msgId is not matching any resource key.");
			}
			return msgId;
		}

		private Evaluateable createEvaluteable(String msgId, String unevaluatedMsg) {
			if (StringUtils.countMatches(unevaluatedMsg, '{') != StringUtils.countMatches(unevaluatedMsg, '}')) {
				LOGGER.warn("Unable to localize '" + msgId + "' with bundle of language '" + getLang()
						+ "': the message '" + unevaluatedMsg
						+ "' is malformatted; it does not contain the same amount of '{' as it does '}'.");
				return new StaticEvaluateable(unevaluatedMsg);
			}
			DynamicEvaluateable rootEvaluateable = new DynamicEvaluateable();
			evaluateInto(rootEvaluateable, unevaluatedMsg);
			return rootEvaluateable.collapse();
		}

		private String evaluateInto(DynamicEvaluateable currentLevelEvaluateable, String msgRest) {
			while (!msgRest.isEmpty()) {
				if (msgRest.charAt(0) == '{') {
					DynamicEvaluateable lowerLevelEvaluateable = new DynamicEvaluateable();
					currentLevelEvaluateable.content.add(lowerLevelEvaluateable);
					msgRest = evaluateInto(lowerLevelEvaluateable, msgRest.substring(1));
				} else if (msgRest.charAt(0) == '}') {
					return msgRest.substring(1);
				} else {
					int nextOpening = msgRest.indexOf('{');
					int nextClosing = msgRest.indexOf('}');
					if (nextOpening == nextClosing) { // CAN ONLY BE IN -1 CASE
						currentLevelEvaluateable.content.add(new StaticEvaluateable(msgRest));
						return StringUtils.EMPTY;
					} else {
						nextOpening = nextOpening == -1 ? Integer.MAX_VALUE : nextOpening;
						nextClosing = nextClosing == -1 ? Integer.MAX_VALUE : nextClosing;
						String content = msgRest.substring(0, Math.min(nextOpening, nextClosing));
						currentLevelEvaluateable.content.add(new StaticEvaluateable(content));
						msgRest = msgRest.substring(content.length());
					}
				}
			}
			return StringUtils.EMPTY;
		}

		private String replaceOrLocalize(String msgId, Map<String, Object> msgParameters) {
			if (msgParameters.containsKey(msgId)) {
				return Objects.toString(msgParameters.get(msgId));
			} else if (this.bundles.containsKey(msgId)) {
				return this.bundles.get(msgId).getString(msgId);
			} else if (msgId.matches(REGEX_TYPICAL_MESSAGE_ID)) {
				LOGGER.warn("Unable to localize '" + msgId + "' with bundle of language '" + getLang()
						+ "': msgId is not matching any resource key.");
			}
			return msgId;
		}

		private String getLang() {
			return toLang(this.locale);
		}
	}

	// #########################################################################################################################################
	// ############################################################### LOCALIZER ###############################################################
	// #########################################################################################################################################

	private final Map<String, LocalizationResource> resourceBundleRegistry;
	private final List<Locale> supportedLocales;

	private Localizer(Map<String, LocalizationResource> resourceBundleRegistry, List<Locale> supportedLocales) {
		this.resourceBundleRegistry = Collections.unmodifiableMap(resourceBundleRegistry);
		this.supportedLocales = Collections.unmodifiableList(supportedLocales);
	}

	static String currentLang() {
		return toLang(VaadinSession.getCurrent().getLocale());
	}

	@Override
	public List<Locale> getProvidedLocales() {
		return this.supportedLocales;
	}

	final boolean canTranslate(String msgId, String lang) {
		if (msgId != null) {
			if (this.resourceBundleRegistry.containsKey(lang)) {
				return this.resourceBundleRegistry.get(lang).hasLocalization(msgId);
			}
		}
		return false;
	}

	@Override
	public String getTranslation(String msgId, Locale locale, Object... params) {
		return getTranslation(msgId, toLang(locale), Collections.emptyMap(), params);
	}

	final String getTranslation(String msgId, String lang, Map<String, ?> namedMsgParameters,
			Object... indexedMsgParameters) {
		if (msgId != null) {
			if (this.resourceBundleRegistry.containsKey(lang)) {
				return this.resourceBundleRegistry.get(lang).renderMessage(msgId, namedMsgParameters,
						indexedMsgParameters);
			} else if (msgId.matches(REGEX_TYPICAL_MESSAGE_ID)) {
				LOGGER.warn("Unable to localize '" + msgId + "'; no bundle for language '" + lang + "'.");
			}
			return msgId;
		} else {
			return null;
		}
	}

	// #########################################################################################################################################
	// ################################################################ BUILDER ################################################################
	// #########################################################################################################################################

	static final class LocalizerBuilder {

		private final Map<String, LocalizationResource> resourceBundleRegistry = new HashMap<>();
		private final Set<Locale> supportedLocales = new HashSet<>();
		private Locale defaultLocale;

		LocalizerBuilder() {
		}

		LocalizerBuilder withLocalization(String baseName, String fileExtension, Charset charset, Locale locale,
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
			LocalizationControl control = new LocalizationControl(charset, fileExtension);
			Set<Locale> uniqueLocales = new HashSet<>();
			uniqueLocales.add(locale);
			uniqueLocales.addAll(Arrays.asList(locales));
			uniqueLocales.remove(null);

			Set<Locale> addedLocales = new HashSet<>();
			Set<String> expectedBundleKeys = new HashSet<>();
			for (Locale loc : uniqueLocales) {
				if (loc != null) {
					checkLocale(loc);

					loc = new Locale(loc.getLanguage(), loc.getCountry());

					ResourceBundle bundle;
					try {
						bundle = ResourceBundle.getBundle(baseName, loc, control);
					} catch (MissingResourceException e) {
						throw new Http901IllegalArgumentException(
								"Unable to find localization class resource '" + baseName + '_' + toLang(loc) + '.'
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
									"The localization resource '" + baseName + '_' + toLang(loc) + '.' + fileExtension
											+ "' for locale " + loc
											+ " differs from the resources of the already analyzed locales "
											+ addedLocales + " regarding the message ids " + difference
											+ "; on differently localed resources of the same base resource, all message id sets have to be equal.");
						}
					}

					String lang = toLang(loc);
					if (!this.resourceBundleRegistry.containsKey(lang)) {
						this.resourceBundleRegistry.put(lang, new LocalizationResource(loc));
						this.supportedLocales.add(loc);
					}
					this.resourceBundleRegistry.get(lang).addBundle(bundle, bundleKeys);
				}
			}
			return this;
		}

		LocalizerBuilder withDefaultLocale(Locale locale) {
			if (locale != null) {
				checkLocale(locale);
			}
			this.defaultLocale = locale;
			return this;
		}

		Localizer build() {
			List<Locale> supportedLocales = new ArrayList<>(this.supportedLocales);
			if (this.defaultLocale != null) {
				supportedLocales.sort((o1, o2) -> this.defaultLocale.equals(o1) ? -1 : 0);
			}
			return new Localizer(this.resourceBundleRegistry, supportedLocales);
		}
	}

	private static void checkLocale(Locale loc) {
		if (StringUtils.isBlank(loc.getISO3Language())) {
			throw new Http901IllegalArgumentException(
					"Cannot register a localization to a locale with a blank ISO3 language.");
		}
	}

	private static String toLang(Locale loc) {
		if (StringUtils.isNotBlank(loc.getISO3Country())) {
			return loc.getISO3Language() + '_' + loc.getISO3Country();
		} else {
			return loc.getISO3Language();
		}
	}
}
