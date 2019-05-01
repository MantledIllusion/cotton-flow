package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.vaadin.metrics.MetricsObserverFlow;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.DefaultDeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CottonServlet extends VaadinServlet {

    public static final String PKEY_DEFAULT_LOCALE = "${cotton.localization.defaultLocale:en}";

    static final String SID_SERVLET = "_servlet";
    static final String SID_DEPLOYMENTCONFIG = "_deploymentConfig";

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

    @Inject
    private Injector servletInjector;

    private final Logger logger = LoggerFactory.getLogger(((Object) this).getClass());

    protected CottonServlet(Blueprint cottonEnvironment) {
        this.servletInjector = Injector.of(cottonEnvironment);
    }

    @Construct
    private CottonServlet() {}

    @Override
    protected final DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {
        return new CottonDeploymentConfiguration(((Object) this).getClass(), initParameters);
    }

    @Override
    protected final VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        VaadinServletService service;
        try {
            // SERVLET
            Blueprint.SingletonAllocation servlet = Blueprint.SingletonAllocation.of(SID_SERVLET, this);

            // DEPLOYMENT CONFIG
            Blueprint.SingletonAllocation deploymentConfig = Blueprint.SingletonAllocation.of(SID_DEPLOYMENTCONFIG,
                    deploymentConfiguration);

            // LOCALIZER
            Map<String, Localizer.LocalizationResource> resourceBundleRegistry = new HashMap<>();
            Set<Locale> supportedLocales = new HashSet<>();

            for (CottonEnvironment.LocalizationRegistration registration:
                    this.servletInjector.aggregate(CottonEnvironment.LocalizationRegistration.class)) {
                String lang = Localizer.toLang(registration.locale);
                if (!resourceBundleRegistry.containsKey(lang)) {
                    resourceBundleRegistry.put(lang, new Localizer.LocalizationResource(registration.locale));
                    supportedLocales.add(registration.locale);
                }
                resourceBundleRegistry.get(lang).addBundle(registration.bundle, registration.bundleKeys);
            }
            List<Locale> supportedLocales2 = new ArrayList<>(supportedLocales);

            Locale defaultLocale = Locale.forLanguageTag(this.servletInjector.resolve(PKEY_DEFAULT_LOCALE));
            Localizer.checkLocale(defaultLocale);
            supportedLocales2.sort((o1, o2) -> defaultLocale.equals(o1) ? -1 : 0);

            Blueprint.SingletonAllocation localizer = Blueprint.SingletonAllocation.of(Localizer.SID_LOCALIZER,
                    new Localizer(resourceBundleRegistry, supportedLocales2));

            // BUILD VAADIN SERVICE
            service = this.servletInjector.instantiate(CottonServletService.class, servlet, deploymentConfig, localizer);
            service.init();

            // METRICS
            MetricsObserverFlow observer = MetricsObserverFlow.observe(service);
            for (CottonEnvironment.MetricsConsumerRegistration registration:
                    this.servletInjector.aggregate(CottonEnvironment.MetricsConsumerRegistration.class)) {
                observer.addConsumer(registration.consumerId, registration.consumer, registration.gate, registration.filter);
            }
        } catch (Exception e) {
            ServiceException se = new ServiceException(e);
            this.logger.error("Unable to create " + CottonServletService.class.getSimpleName() + " for "
                    + ((Object) this).getClass().getSimpleName(), se);
            throw se;
        }

        return service;
    }

    @Override
    public final void destroy() {
        super.destroy();
        if (this.servletInjector instanceof Injector.RootInjector) {
            ((Injector.RootInjector) this.servletInjector).shutdown();
        }
    }
}
