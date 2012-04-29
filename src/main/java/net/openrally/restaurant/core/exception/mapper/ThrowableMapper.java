package net.openrally.restaurant.core.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ThrowableMapper extends BaseExceptionMapper implements
		ExceptionMapper<Throwable> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Response toResponse(Throwable exception) {
		logger.debug("ThrowableMapper captured an exception: " + exception);

		Error error = new Error();
		error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.setMessage(exception.getMessage().isEmpty() ? "Internal Server Error"
				: exception.getMessage());

		ResponseBuilder responseBuilder = Response
				.status(Response.Status.INTERNAL_SERVER_ERROR);
		responseBuilder.type(MediaType.APPLICATION_JSON);
		responseBuilder.entity(gson.toJson(error));

		return responseBuilder.build();
	}

}
