package com.mantledillusion.vaadin.cotton.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * {@link RuntimeException} sub type that is used for all Cotton internal
 * {@link Exception}s; may be extended for own {@link Exception}s if desired.
 */
public abstract class WebException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String WEB_EXCEPTION_NAME_CONVENTION = "Http\\d{3}[a-zA-Z]*Exception";

	private final int httpCode;
	private final String name;
	private final String defaultMessage;

	protected WebException(String defaultMessage) {
		this(defaultMessage, null, null);
	}

	protected WebException(String defaultMessage, String message) {
		this(defaultMessage, message, null);
	}

	protected WebException(String defaultMessage, String message, Exception e) {
		super(message, e);

		if (!getClass().getSimpleName().matches(WEB_EXCEPTION_NAME_CONVENTION)) {
			throw new RuntimeException("The " + WebException.class.getSimpleName() + " extension "
					+ getClass().getSimpleName() + "'s class name does not match the convention for such extensions: '"
					+ WEB_EXCEPTION_NAME_CONVENTION + "'");
		}

		this.httpCode = Integer.parseInt(getClass().getSimpleName().substring(4, 7));

		String exceptionName = getClass().getSimpleName().substring(7, getClass().getSimpleName().length() - 9);
		this.name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(exceptionName), StringUtils.SPACE);

		this.defaultMessage = defaultMessage;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return WebException.class.getSimpleName() + " (http " + this.httpCode + " - " + this.name + "): "
				+ this.defaultMessage + (StringUtils.isBlank(getMessage()) ? StringUtils.EMPTY : "; " + getMessage());
	}
}
