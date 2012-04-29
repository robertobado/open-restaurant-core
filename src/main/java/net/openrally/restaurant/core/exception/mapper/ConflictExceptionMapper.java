package net.openrally.restaurant.core.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import net.openrally.restaurant.core.exception.ConflictException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Provider
public class ConflictExceptionMapper extends BaseExceptionMapper implements
ExceptionMapper<ConflictException> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Response toResponse(ConflictException exception) {
		logger.debug("ConflictExceptionMapper captured an exception: " + exception);
		
		Error error = new Error();
		error.setStatus(HttpStatus.CONFLICT.value());
		error.setMessage(exception.getMessage());
				
		ResponseBuilder responseBuilder = Response.status(Response.Status.CONFLICT);
 		responseBuilder.type(MediaType.APPLICATION_JSON);
 		responseBuilder.entity(gson.toJson(error));
		
 		return responseBuilder.build();	
	}
}
