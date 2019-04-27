package com.mantledillusion.vaadin.cotton.exception.http900;

import com.mantledillusion.vaadin.cotton.exception.WebException;

public class Http906InjectionErrorException extends WebException {

	private static final long serialVersionUID = 1L;

	public Http906InjectionErrorException(String message, Exception e) {
		super("An error occurred during an injection", message, e);
	}
}
