package net.openrally.restaurant.core.exposure.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.exception.ConflictException;
import net.openrally.restaurant.core.exception.ForbiddenException;
import net.openrally.restaurant.core.exception.NotFoundException;
import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.persistence.dao.ConfigurationDAO;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.request.body.ConfigurationRequestBody;
import net.openrally.restaurant.response.body.ConfigurationResponseBody;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("/configuration")
@Component
@Transactional
public class ConfigurationResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String PATH = "configuration";
	
	@Autowired
	private ConfigurationDAO configurationDAO;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response get(@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		User user = getRequestUser(loginToken);

		Configuration entity = configurationDAO.loadByCompanyId(user.getCompany().getCompanyId());

		ConfigurationResponseBody entityResponseBody = new ConfigurationResponseBody(entity);

		return Response.ok(gson.toJson(entityResponseBody)).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response put(String requestBody,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			URISyntaxException, UnauthorizedException, ConflictException {

		User user = getRequestUser(loginToken);

		ConfigurationRequestBody entityRequestBody = retrieveEntityRequestBody(requestBody);
		
		Configuration entity = configurationDAO.loadByCompanyId(user.getCompany().getCompanyId());
		
		entity.setLoginTokenLifeTime(entityRequestBody.getLoginTokenLifeTime());
		entity.setCompanyName(entityRequestBody.getCompanyName());
		entity.setBillTemplate(entityRequestBody.getBillTemplate());

		logger.debug("Updating configuration");

		try {
			configurationDAO.update(entity);
			configurationDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH);

		logger.debug("Finished processing request successfully");

		return Response.ok().contentLocation(locationURI).build();
	}
	
	private ConfigurationRequestBody retrieveEntityRequestBody(String requestBodyString)
			throws BadRequestException {
		ConfigurationRequestBody entityRequestBody;

		try {
			entityRequestBody = gson.fromJson(requestBodyString,
					ConfigurationRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == entityRequestBody) {
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}

		entityRequestBody.validate();

		return entityRequestBody;
	}

}
