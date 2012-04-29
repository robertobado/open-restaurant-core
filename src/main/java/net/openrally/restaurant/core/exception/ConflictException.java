package net.openrally.restaurant.core.exception;

public class ConflictException extends Exception {

	private static final long serialVersionUID = -410019407372087949L;


	public ConflictException(String message) {
		super(message);
	}
	
	public ConflictException() {
		super("");
	}
}
