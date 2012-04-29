package net.openrally.restaurant.core.exception;

public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = -3578470207193869550L;

	public UnauthorizedException(String message){
		super(message);
	}
	
	public UnauthorizedException() {
		super("");
	}
}
