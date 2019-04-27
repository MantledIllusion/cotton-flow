package com.mantledillusion.vaadin.cotton.event.user;

import java.util.EventObject;

import com.mantledillusion.vaadin.cotton.User;

/**
 * Event that is dispatched after a new {@link User} logged in.
 */
public class AfterLoginEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public AfterLoginEvent(Object source) {
		super(source);
	}
}
