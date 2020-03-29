package com.mantledillusion.vaadin.cotton.event.responsive;

import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive;
import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive.Alternative;

import java.util.EventObject;

/**
 * Event that is dispatched before a the current @{@link Responsive} view is exchanged for an @{@link Alternative}.
 * <p>
 * Exchanging might be declined by calling {@link #decline()}.
 */
public class BeforeResponsiveRefreshEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private Alternative.AdaptionMode adaptionMode;

    public BeforeResponsiveRefreshEvent(Object source, Alternative.AdaptionMode adaptionMode) {
        super(source);
        this.adaptionMode = adaptionMode;
    }

    /**
     * Marks the requested @{@link Alternative} exchange to be declined.
     */
    public void decline() {
        this.adaptionMode = Alternative.AdaptionMode.combine(this.adaptionMode, Alternative.AdaptionMode.PROHIBIT);
    }

    /**
     * Returns whether the announced @{@link Alternative} exchange is forced, so it will commence no matter what.
     *
     * @return True if the exchange will commence no matter what, false otherwise
     */
    public boolean isForced() {
        return this.adaptionMode == Alternative.AdaptionMode.ENFORCE;
    }

    /**
     * Returns whether the announced @{@link Alternative} exchange is accepted by all retrievers of this event.
     *
     * @return True if no retriever has called {@link #decline()}, false otherwise
     */
    public boolean isAccepted() {
        return this.adaptionMode == Alternative.AdaptionMode.PERFORM || this.adaptionMode == Alternative.AdaptionMode.ENFORCE;
    }
}
