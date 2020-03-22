package com.mantledillusion.vaadin.cotton.exception.http500;

import com.mantledillusion.vaadin.cotton.exception.WebException;

public class Http500InternalServerErrorException extends WebException {

	private static final long serialVersionUID = 1L;

	public Http500InternalServerErrorException(String message) {
		super("An internal error occurred", message);
	}

	public Http500InternalServerErrorException(String message, Exception e) {
		super("An internal error occurred", message, e);
	}
}
