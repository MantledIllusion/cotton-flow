package com.mantledillusion.vaadin.cotton.exception.http900;

import com.mantledillusion.vaadin.cotton.exception.WebException;
import com.vaadin.flow.server.VaadinSession;

/**
 * Framework error code; Error that may occur when trying to statically execute
 * functions on the current {@link VaadinSession} when there is none.
 */
public class Http900NoSessionContextException extends WebException {

	private static final long serialVersionUID = 1L;

	public Http900NoSessionContextException() {
		super("Not in the context of a " + VaadinSession.class.getSimpleName());
	}
}
