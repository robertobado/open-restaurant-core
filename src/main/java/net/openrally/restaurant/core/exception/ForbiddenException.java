package net.openrally.restaurant.core.exception;

public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 1000170418664297037L;

	public ForbiddenException() {
		super("");
	}

	public ForbiddenException(String message) {
		super(message);
	}
}
