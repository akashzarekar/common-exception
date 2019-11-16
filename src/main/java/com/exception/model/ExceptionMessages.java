package com.exception.model;

public enum ExceptionMessages {
	FIELD_VALIDATION_FAILED("VALIDATION FAILED");

	private String message;

	private ExceptionMessages(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
