package com.mantledillusion.vaadin.cotton.event.user;

import java.util.EventObject;

import com.mantledillusion.vaadin.cotton.User;

/**
 * Event that is dispatched before a new {@link User} logs out.
 * <p>
 * Logging out might be declined by calling {@link #decline()}.
 */
public class BeforeLogoutEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private boolean doAccept = true;

	public BeforeLogoutEvent(Object source) {
		super(source);
	}

	/**
	 * Marks the requested user change to be declined.
	 */
	public void decline() {
		this.doAccept = false;
	}

	/**
	 * Returns whether the announced user change is accepted by all retrievers of
	 * this event.
	 *
	 * @return True if no retriever has called {@link #decline()}, false otherwise
	 */
	public boolean isAccepted() {
		return doAccept;
	}
}
