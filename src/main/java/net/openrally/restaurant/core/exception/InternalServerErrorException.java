package net.openrally.restaurant.core.exception;

public class InternalServerErrorException extends RuntimeException {

	private static final long serialVersionUID = -2954265076055471238L;

	public InternalServerErrorException(String message){
		super(message);
	}

	public InternalServerErrorException() {
		super("");
	}
}
