package com.mantledillusion.vaadin.cotton;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.DefaultDeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Extended {@link VaadinServlet} that sets up Cotton's application environment.
 * <p>
 * In application who are completely injected by Hura, this {@link javax.servlet.Servlet} class can be injected directly.
 * <p>
 * In other environments (such as Spring or plain Servlet-API deployments), this class has to be extended,
 * so the protected constructor is used with an instance of the {@link Blueprint} that defines the application's environment.
 */
public class CottonServlet extends VaadinServlet {

    private static final String PKEY_HURAWEB_INITIALIZER = "hura.web.application.initializerClass";
    private static final String PKEY_HURAWEB_BASEPACKAGE = "hura.web.application.basePackage";

    static final String SID_SERVLET = "_servlet";
    static final String SID_DEPLOYMENTCONFIG = "_deploymentConfig";
    static final String PID_INITIALIZERCLASS = "_initializerClass";
    static final String PID_BASEPACKAGE = "_basePackage";

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

    private final Logger logger = LoggerFactory.getLogger(((Object) this).getClass());
    private final Injector servletInjector;
    private final String applicationInitializerClass;
    private final String applicationPackage;

    /**
     * Constructor when Cotton is running in a non-Hura injected application.
     *
     * @param cottonEnvironment The {@link Blueprint} that @{@link com.mantledillusion.injection.hura.core.annotation.instruction.Define}s
     *                          the application's environment; might <b>not</b> be null.
     */
    protected CottonServlet(Blueprint cottonEnvironment) {
        if (cottonEnvironment == null) {
            throw new Http901IllegalArgumentException("Cannot initialize a " + CottonServlet.class.getSimpleName() + " using a null cotton environment blueprint");
        }

        this.servletInjector = Injector.of(cottonEnvironment);
        this.applicationInitializerClass = cottonEnvironment.getClass().getName();
        this.applicationPackage = this.servletInjector.resolve("${" + CottonEnvironment.PKEY_APPLICATION_BASE_PACKAGE
                + ":"+cottonEnvironment.getClass().getPackage().getName() + "}", true);
    }

    @Construct
    private CottonServlet(@Inject Injector servletInjector,
                          @Resolve("${" + PKEY_HURAWEB_INITIALIZER + "}") String applicationInitializerClass,
                          @Resolve("${" + CottonEnvironment.PKEY_APPLICATION_BASE_PACKAGE + ":${"
                                  + PKEY_HURAWEB_BASEPACKAGE + "}}") String applicationPackage) {
        this.servletInjector = servletInjector;
        this.applicationInitializerClass = applicationInitializerClass;
        this.applicationPackage = applicationPackage;
    }

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

            Locale defaultLocale = Locale.forLanguageTag(this.servletInjector.resolve("${"+ CottonEnvironment.PKEY_DEFAULT_LOCALE+":en}"));
            Localizer.checkLocale(defaultLocale);
            supportedLocales2.sort((o1, o2) -> defaultLocale.equals(o1) ? -1 : 0);

            Blueprint.SingletonAllocation localizer = Blueprint.SingletonAllocation.of(Localizer.SID_LOCALIZER,
                    new Localizer(resourceBundleRegistry, supportedLocales2));

            // APPLICATION
            Blueprint.PropertyAllocation initializerClass = Blueprint.PropertyAllocation.of(PID_INITIALIZERCLASS, this.applicationInitializerClass);
            Blueprint.PropertyAllocation basePackage = Blueprint.PropertyAllocation.of(PID_BASEPACKAGE, this.applicationPackage);

            // BUILD VAADIN SERVICE
            service = this.servletInjector.instantiate(CottonServletService.class, servlet, deploymentConfig, localizer,
                    initializerClass, basePackage);
            service.init();
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
