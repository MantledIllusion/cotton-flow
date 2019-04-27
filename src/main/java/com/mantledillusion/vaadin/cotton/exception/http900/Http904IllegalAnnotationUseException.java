package com.mantledillusion.vaadin.cotton.exception.http900;

import com.mantledillusion.vaadin.cotton.exception.WebException;

/**
 * Framework error code; Error that may occur when a framework annotation is
 * used in a place or way it is not destined for.
 */
public class Http904IllegalAnnotationUseException extends WebException {

	private static final long serialVersionUID = 1L;

	public Http904IllegalAnnotationUseException(String message) {
		this(message, null);
	}

	public Http904IllegalAnnotationUseException(String message, Exception e) {
		super("Framework annotation not used as expected", message, e);
	}
}
