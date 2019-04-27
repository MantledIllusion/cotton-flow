package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.Predefinable;
import com.mantledillusion.injection.hura.annotation.Global.SingletonMode;
import com.mantledillusion.vaadin.cotton.Localizer.LocalizerBuilder;
import com.mantledillusion.vaadin.cotton.exception.http900.Http902IllegalStateException;
import com.mantledillusion.vaadin.cotton.viewpresenter.Restricted;
import com.mantledillusion.vaadin.cotton.viewpresenter.View;
import com.mantledillusion.vaadin.metrics.MetricsConsumer;
import com.mantledillusion.vaadin.metrics.MetricsObserverFlow;
import com.mantledillusion.vaadin.metrics.MetricsPredicate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.DefaultDeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;

import javax.servlet.Servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Servlet} that serves as Cotton's configuration.
 * <p>
 * Cotton is set up by extending this {@link CottonServlet} and overriding
 * {@link #configure(TemporalCottonServletConfiguration)}.
 */
public abstract class CottonServlet extends VaadinServlet {

	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final class CottonDeploymentConfiguration extends DefaultDeploymentConfiguration {

		private static final long serialVersionUID = 1L;

		public CottonDeploymentConfiguration(Class<?> systemPropertyBaseClass, Properties initParameters) {
			super(systemPropertyBaseClass, initParameters);
		}

		@Override
		public String getUIClassName() {
			return CottonUI.class.getName();
		}
	}

	private final class ConsumerRegistration {

		private final String consumerId;
		private final MetricsConsumer consumer;
		private final MetricsPredicate gate;
		private final MetricsPredicate filter;

		public ConsumerRegistration(String consumerId, MetricsConsumer consumer, MetricsPredicate gate,
				MetricsPredicate filter) {
			this.consumerId = consumerId;
			this.consumer = consumer;
			this.gate = gate;
			this.filter = filter;
		}
	}

	/**
	 * Temporarily active configuration type that can be used to configure a
	 * {@link CottonServlet}.
	 * <P>
	 * May only be used during the configuration phase of the {@link CottonServlet}
	 * it is given to.
	 */
	protected final class TemporalCottonServletConfiguration {

		private boolean allowConfiguration = true;

		// NAVIGATION
		private final Set<Class<? extends Component>> views;

		// LOCALIZATION
		private final LocalizerBuilder localizerBuilder;

		// LOGIN
		private LoginProvider loginProvider;

		// BEANS
		private List<Predefinable> predefinables = new ArrayList<>();

		// METRICS
		private List<ConsumerRegistration> metricsConsumers = new ArrayList<>();

		private TemporalCottonServletConfiguration() {
			this.views = new HashSet<>();
			this.localizerBuilder = new LocalizerBuilder();
		}

		private void checkConfigurationAllowed() {
			if (!this.allowConfiguration) {
				throw new Http902IllegalStateException(
						"Configuration may only be done during the configuration phase of an UI.");
			}
		}

		/**
		 * Registers the given {@link View} implementation.
		 * 
		 * @param viewClass
		 *            The {@link View} implementation to register; might <b>not</b> be
		 *            null.
		 * @return this
		 */
		public TemporalCottonServletConfiguration registerViewResource(Class<? extends View> viewClass) {
			this.views.add(viewClass);
			return this;
		}

		Set<Class<? extends Component>> getViews() {
			return this.views;
		}

		/**
		 * Uses combinations of the given base name and a single locale to create
		 * {@link ResourceBundle}s.
		 * <P>
		 * The String
		 * baseName+'_'+{@link Locale#getISO3Language()}+{@link Locale#getISO3Country()}+'.'+fileExtension
		 * will be used to look for a {@link Class} resource file.
		 *
		 * @param baseName
		 *            The base file name that should be used to build resource file
		 *            names; <b>not</b> allowed to be null or blank.
		 * @param fileExtension
		 *            The file extension that should be used to build resource file
		 *            names; <b>not</b> allowed to be null or blank.
		 * @param charset
		 *            The {@link Charset} to use to retrieve the resource file's
		 *            content, like 'UTF8' etc; <b>not</b> allowed to be null.
		 * @param locale
		 *            The first {@link Locale}s to find resource files for; <b>not</b>
		 *            allowed to be null.
		 * @param locales
		 *            Additional {@link Locale}s to find resource files for; might be
		 *            null, empty or contain nulls.
		 * @return this
		 */
		public TemporalCottonServletConfiguration registerLocalization(String baseName, String fileExtension,
				Charset charset, Locale locale, Locale... locales) {
			checkConfigurationAllowed();
			this.localizerBuilder.withLocalization(baseName, fileExtension, charset, locale, locales);
			return this;
		}

		/**
		 * Sets the default language of the {@link CottonServlet} to the given
		 * {@link Locale}.
		 * <P>
		 * When a user visits the {@link CottonServlet} without a language specified or
		 * with one where there is no {@link ResourceBundle} registered for, the
		 * language will automatically be switched to this default language.
		 *
		 * @param locale
		 *            The {@link Locale} to use as default; may be null.
		 * @return this
		 */
		public TemporalCottonServletConfiguration setDefaultLocale(Locale locale) {
			this.localizerBuilder.withDefaultLocale(locale);
			return this;
		}

		LocalizerBuilder getLocalizerBuilder() {
			return this.localizerBuilder;
		}

		/**
		 * Registers the given {@link LoginProvider} to be used for automatic login; for
		 * example when a @{@link Restricted} {@link View} is visited.
		 * 
		 * @param loginProvider
		 *            The login provider to register; might be null
		 * @return this
		 */
		public TemporalCottonServletConfiguration registerLoginProvider(LoginProvider loginProvider) {
			checkConfigurationAllowed();
			this.loginProvider = loginProvider;
			return this;
		}

		LoginProvider getLoginProvider() {
			return loginProvider;
		}

		/**
		 * Registers the given {@link Predefinable}s (such as
		 * {@link Predefinable.Property}s or {@link SingletonMode#GLOBAL}
		 * {@link Predefinable.Singleton}s) to be available in every injected
		 * {@link View} (and its injected beans).
		 *
		 * @param predefinables
		 *            The predefinables to register; might be null or contain nulls,
		 *            both is ignored.
		 * @return this
		 */
		public TemporalCottonServletConfiguration registerPredefinables(Predefinable... predefinables) {
			if (predefinables != null) {
				for (Predefinable predefinable : predefinables) {
					this.predefinables.add(predefinable);
				}
			}
			return this;
		}

		List<Predefinable> getPredefinables() {
			return this.predefinables;
		}

		/**
		 * Registers the given {@link MetricsConsumer}s to Cotton's
		 * {@link MetricsObserverFlow}.
		 * 
		 * @param consumerId
		 *            The unique id to register the consumer under; might <b>not</b> be
		 *            null.
		 * @param metricsConsumer
		 *            The consumer to register; might <b>not</b> be null.
		 * @return this
		 */
		public TemporalCottonServletConfiguration registerMetricsConsumers(String consumerId,
				MetricsConsumer metricsConsumer) {
			return registerMetricsConsumers(consumerId, metricsConsumer, null, null);
		}

		/**
		 * Registers the given {@link MetricsConsumer}s to Cotton's
		 * {@link MetricsObserverFlow}.
		 * 
		 * @param consumerId
		 *            The unique id to register the consumer under; might <b>not</b> be
		 *            null.
		 * @param metricsConsumer
		 *            The consumer to register; might <b>not</b> be null.
		 * @param gate
		 *            The predicate that needs to MetricsPredicate.test(MetricEvent)
		 *            true to trigger flushing all of a session's accumulated
		 *            MetricEvents; might be null.
		 * @param filter
		 *            The predicate that needs to MetricsPredicate.test(MetricEvent)
		 *            true to allow an about-to-be-flushed event to be delivered to the
		 *            consumer; might be null.
		 * @return this
		 */
		public TemporalCottonServletConfiguration registerMetricsConsumers(String consumerId,
				MetricsConsumer metricsConsumer, MetricsPredicate gate, MetricsPredicate filter) {
			this.metricsConsumers.add(new ConsumerRegistration(consumerId, metricsConsumer, gate, filter));
			return this;
		}
	}

	@Override
	protected final DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {
		return new CottonDeploymentConfiguration(getClass(), initParameters);
	}

	@Override
	protected final VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		TemporalCottonServletConfiguration config = new TemporalCottonServletConfiguration();
		VaadinServletService service;
		try {
			configure(config);
			service = new CottonServletService(this, deploymentConfiguration, config);
			service.init();

			MetricsObserverFlow observer = MetricsObserverFlow.observe(service);
			config.metricsConsumers.forEach(registration -> observer.addConsumer(registration.consumerId,
					registration.consumer, registration.gate, registration.filter));
		} catch (Exception e) {
			ServiceException se = new ServiceException(e);
			this.logger.error("Unable to create " + CottonServletService.class.getSimpleName() + " for "
					+ getClass().getSimpleName(), se);
			throw se;
		} finally {
			config.allowConfiguration = false;
		}

		return service;
	}

	/**
	 * Configures the {@link CottonServlet} on startup using the given
	 * {@link TemporalCottonServletConfiguration}.
	 * <P>
	 * The given {@link TemporalCottonServletConfiguration} instance may only be
	 * used during the call of this {@link Method}.
	 *
	 * @param config
	 *            The {@link TemporalCottonServletConfiguration} to use for
	 *            configuration; <b>not</b> allowed to be null.
	 */
	protected abstract void configure(TemporalCottonServletConfiguration config);
}
