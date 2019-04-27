package com.mantledillusion.vaadin.cotton.exception.http900;

import com.mantledillusion.vaadin.cotton.exception.WebException;

/**
 * Framework error code; Error that may occur when calling a function that is
 * part of the framework, but the framework is in a current state that does not
 * allow that function; equals {@link IllegalStateException}.
 */
public class Http902IllegalStateException extends WebException {

	private static final long serialVersionUID = 1L;

	public Http902IllegalStateException(String message) {
		super("Illegal State", message);
	}
}
