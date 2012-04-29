package net.openrally.restaurant.core.exception;

public class BadRequestException extends Exception {
	
	private static final long serialVersionUID = -5621757131585899954L;

	public BadRequestException(String message) {
		super(message);
	}
	
	public BadRequestException() {
		super("");
	}
}
