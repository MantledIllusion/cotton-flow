package com.mantledillusion.vaadin.cotton;

import com.helger.css.ECSSUnit;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.metrics.trail.MetricsTrailSupport;
import com.mantledillusion.metrics.trail.api.Metric;
import com.mantledillusion.metrics.trail.api.MetricAttribute;
import com.mantledillusion.vaadin.cotton.component.builders.DialogBuilder;
import com.mantledillusion.vaadin.cotton.component.builders.HorizontalLayoutBuilder;
import com.mantledillusion.vaadin.cotton.component.builders.LabelBuilder;
import com.mantledillusion.vaadin.cotton.component.builders.VerticalLayoutBuilder;
import com.mantledillusion.vaadin.cotton.component.css.CssStyle;
import com.mantledillusion.vaadin.cotton.exception.WebException;
import com.mantledillusion.vaadin.cotton.exception.http900.Http902IllegalStateException;
import com.mantledillusion.vaadin.cotton.metrics.CottonMetrics;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

class CottonErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CottonErrorHandler.class);
    private static final Supplier<Icon> ERROR_ICON_SUPPLIER = () -> {
        Icon icon = VaadinIcon.FIRE.create();
        CssStyle.COLOR.ofHSB(0, 1, 0.75).apply(icon);
        return icon;
    };
    private static final CottonErrorContentProvider DEFAULT_PROVIDER = new CottonErrorContentProvider(Throwable.class,
            (injector, httpCode, error, errorMessage) ->  VerticalLayoutBuilder.create().
                    setSizeUndefined().
                    setPadding(false).
                    setSpacing(true).
                    add(HorizontalLayoutBuilder.create().
                            setWidthUndefined().
                            setPadding(false).
                            setSpacing(true).
                            add(FlexComponent.Alignment.CENTER, ERROR_ICON_SUPPLIER.get()).
                            add(LabelBuilder.create().
                                    setWidthFull().
                                    setText(httpCode+": "+errorMessage).
                                    setWrap(false).
                                    build()).
                            build()).
                    add(FlexComponent.Alignment.CENTER, LabelBuilder.create().
                            setText("TrailId: "+MetricsTrailSupport.id().toString()).
                            setCssStyle(CssStyle.FONT_SIZE.of(0.8, ECSSUnit.EM)).
                            setCssStyle(CssStyle.COLOR.ofHSB(0, 0, 0.5)).
                            setWrap(false).
                            build()).
                    build());

    static class CottonErrorView extends Div implements HasErrorParameter<Exception>, BeforeLeaveListener {

        @Inject
        @Qualifier(CottonErrorHandler.SID_ERROR_HANDLER)
        private CottonErrorHandler errorHandler;
        @Inject
        private Injector injector;

        private final Registration registration;
        private Dialog errorDialog;

        @Construct
        private CottonErrorView() {
            this.registration = CottonUI.current().addBeforeLeaveListener(this);
        }

        @Override
        public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
            LOGGER.error("UI navigation error occurred (TrailId: "+MetricsTrailSupport.get().getTrailId()+")",
                    parameter.getCaughtException());
            this.errorHandler.writeMetric(parameter.getCaughtException(), parameter.getCustomMessage());

            int httpCode = extractHttpCode(parameter.getCaughtException());
            this.errorDialog = DialogBuilder.createBasic(this.errorHandler.
                    determineContent(injector, httpCode, parameter.getCaughtException(), parameter.getCustomMessage())).
                    setCloseOnOutsideClick(false).
                    setCloseOnEsc(false).
                    build();
            CssStyle.MAX_WIDTH.of(500, ECSSUnit.PX).apply(this.errorDialog);
            this.errorDialog.open();
            return httpCode;
        }

        @Override
        public void beforeLeave(BeforeLeaveEvent event) {
            this.errorDialog.close();
            this.registration.remove();
        }
    }

    interface InjectingErrorRenderer {

        Component display(Injector injector, int httpCode, Throwable t, String message);
    }

    static class CottonErrorContentProvider {

        private final Class<? extends Throwable> throwableType;
        private final InjectingErrorRenderer renderer;

        CottonErrorContentProvider(Class<? extends Throwable> throwableType,
                                   InjectingErrorRenderer renderer) {
            this.throwableType = throwableType;
            this.renderer = renderer;
        }
    }

    static final String SID_ERROR_HANDLER = "_errorHandler";

    private final Map<Class<? extends Throwable>, CottonErrorContentProvider> contentProviders;

    public CottonErrorHandler(Collection<CottonErrorContentProvider> errorContentProviders) {
        Map<Class<? extends Throwable>, CottonErrorContentProvider> contentProviders = new HashMap<>();
        for (CottonErrorContentProvider provider: errorContentProviders) {
            if (contentProviders.containsKey(provider.throwableType)) {
                throw new Http902IllegalStateException("There are multiple error content providers registered for the " +
                        "throwable type " + provider.throwableType.getSimpleName());
            }
            contentProviders.put(provider.throwableType, provider);
        }
        this.contentProviders = Collections.unmodifiableMap(contentProviders);
    }

    @Override
    public void error(ErrorEvent event) {
        LOGGER.error("UI on-page error occurred (TrailId: "+MetricsTrailSupport.get().getTrailId()+")",
                event.getThrowable());
        writeMetric(event.getThrowable(), event.getThrowable().getMessage());

        Injector injector = CottonSession.current().createInSessionContext(Injector.class);
        int httpCode = extractHttpCode(event.getThrowable());
        Dialog errorDialog = DialogBuilder.createBasic(
                determineContent(injector, httpCode, event.getThrowable(), event.getThrowable().getMessage())).
                addOption(VaadinIcon.REFRESH.create(), e -> {
                    CottonSession.current().destroyInSessionContext(injector);
                    CottonUI.current().getPage().reload();
                }).
                setCloseOnOutsideClick(false).
                setCloseOnEsc(false).
                build();
        CssStyle.MAX_WIDTH.of(500, ECSSUnit.PX).apply(errorDialog);
        errorDialog.open();
    }

    private static int extractHttpCode(Throwable t) {
        if (t instanceof WebException) {
            return ((WebException) t).getHttpCode();
        } else if (t instanceof NotFoundException) {
            return 404;
        } else {
            return 500;
        }
    }

    private void writeMetric(Throwable t, String message) {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        t.printStackTrace(writer);

        Metric metric = CottonMetrics.SESSION_ERROR.build(t.getClass().getSimpleName());
        metric.getAttributes().add(new MetricAttribute("type", t.getClass().getName()));
        metric.getAttributes().add(new MetricAttribute("message", message));
        MetricsTrailSupport.commit(metric);
    }

    private Component determineContent(Injector injector, int httpCode, Throwable t, String message) {
        Class<? extends Throwable> errorType = t.getClass();
        do {
            if (this.contentProviders.containsKey(errorType)) {
                return this.contentProviders.get(errorType).renderer.display(injector, httpCode, t, message);
            }
            errorType = (Class<? extends Throwable>) errorType.getSuperclass();
        } while (errorType != Throwable.class);
        return DEFAULT_PROVIDER.renderer.display(injector, httpCode, t, message);
    }
}
