package net.openrally.restaurant.core.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import net.openrally.restaurant.core.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Provider
public class NotFoundExceptionMapper extends BaseExceptionMapper implements
ExceptionMapper<NotFoundException>{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Response toResponse(NotFoundException exception) {
	logger.debug("NotFoundExceptionMapper captured an exception: " + exception);

	Error error = new Error();
	error.setStatus(HttpStatus.NOT_FOUND.value());
	error.setMessage(exception.getMessage().isEmpty() ? "Not Found"
			: exception.getMessage());

	ResponseBuilder responseBuilder = Response
			.status(Response.Status.NOT_FOUND);
	responseBuilder.type(MediaType.APPLICATION_JSON);
	responseBuilder.entity(gson.toJson(error));

	return responseBuilder.build();
	}
}