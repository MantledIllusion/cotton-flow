package com.mantledillusion.vaadin.cotton.exception.http900;

import com.mantledillusion.vaadin.cotton.exception.WebException;

/**
 * Framework error code; Error that may occur when calling a function that is
 * part of the framework, but the given arguments do not fit the requirements;
 * equals {@link IllegalArgumentException}.
 */
public class Http901IllegalArgumentException extends WebException {

	private static final long serialVersionUID = 1L;

	public Http901IllegalArgumentException(String message) {
		this(message, null);
	}

	public Http901IllegalArgumentException(String message, Exception e) {
		super("Illegal Argument", message);
	}
}
