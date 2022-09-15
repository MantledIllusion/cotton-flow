package com.mantledillusion.vaadin.cotton.event.responsive;

import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive;
import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive.ScreenClass;

import java.util.EventObject;

/**
 * Event that is dispatched before a the current @{@link Responsive} view is exchanged for an @{@link ScreenClass}.
 * <p>
 * Exchanging might be declined by calling {@link #decline()}.
 */
public class BeforeResponsiveRefreshEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private ScreenClass.AdaptionMode adaptionMode;

    public BeforeResponsiveRefreshEvent(Object source, ScreenClass.AdaptionMode adaptionMode) {
        super(source);
        this.adaptionMode = adaptionMode;
    }

    /**
     * Marks the requested @{@link ScreenClass} exchange to be declined.
     */
    public void decline() {
        this.adaptionMode = ScreenClass.AdaptionMode.combine(this.adaptionMode, ScreenClass.AdaptionMode.PROHIBIT);
    }

    /**
     * Returns whether the announced @{@link ScreenClass} exchange is forced, so it will commence no matter what.
     *
     * @return True if the exchange will commence no matter what, false otherwise
     */
    public boolean isForced() {
        return this.adaptionMode == ScreenClass.AdaptionMode.ENFORCE;
    }

    /**
     * Returns whether the announced @{@link ScreenClass} exchange is accepted by all retrievers of this event.
     *
     * @return True if no retriever has called {@link #decline()}, false otherwise
     */
    public boolean isAccepted() {
        return this.adaptionMode == ScreenClass.AdaptionMode.PERFORM || this.adaptionMode == ScreenClass.AdaptionMode.ENFORCE;
    }
}
