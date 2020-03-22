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

    private boolean doAccept = true;

    public BeforeResponsiveRefreshEvent(Object source) {
        super(source);
    }

    /**
     * Marks the requested @{@link Alternative} exchange to be declined.
     */
    public void decline() {
        this.doAccept = false;
    }

    /**
     * Returns whether the announced @{@link Alternative} exchange is accepted by all retrievers of this event.
     *
     * @return True if no retriever has called {@link #decline()}, false otherwise
     */
    public boolean isAccepted() {
        return doAccept;
    }
}
