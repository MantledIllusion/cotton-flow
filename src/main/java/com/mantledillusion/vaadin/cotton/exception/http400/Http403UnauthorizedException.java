package com.mantledillusion.vaadin.cotton.exception.http400;

import com.mantledillusion.vaadin.cotton.exception.WebException;

/**
 * HTTP standard code; TODO
 */
public class Http403UnauthorizedException extends WebException {

	private static final long serialVersionUID = 1L;

	public Http403UnauthorizedException(String message) {
		super("User is not authorized to perform action", message);
	}
}
