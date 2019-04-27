package com.mantledillusion.vaadin.cotton.exception.http900;

import com.mantledillusion.vaadin.cotton.exception.WebException;

public class Http903NotImplementedException extends WebException {

	private static final long serialVersionUID = 1L;

	public Http903NotImplementedException(String message) {
		super("Functionality not implemented", message);
	}
}
