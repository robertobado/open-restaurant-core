package net.openrally.restaurant.core.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import net.openrally.restaurant.core.exception.BadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Provider
public class BadRequestExceptionMapper extends BaseExceptionMapper implements
		ExceptionMapper<BadRequestException> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Response toResponse(BadRequestException exception) {
		logger.debug("BadRequestExceptionMapper captured an exception: "
				+ exception);

		Error error = new Error();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMessage(exception.getMessage());

		ResponseBuilder responseBuilder = Response
				.status(Response.Status.BAD_REQUEST);
		responseBuilder.type(MediaType.APPLICATION_JSON);
		responseBuilder.entity(gson.toJson(error));

		return responseBuilder.build();
	}

}
