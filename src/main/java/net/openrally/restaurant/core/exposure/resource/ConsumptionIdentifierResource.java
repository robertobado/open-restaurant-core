package net.openrally.restaurant.core.exposure.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.exception.ConflictException;
import net.openrally.restaurant.core.exception.ForbiddenException;
import net.openrally.restaurant.core.exception.NotFoundException;
import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.persistence.dao.ConsumptionIdentifierDAO;
import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.request.body.ConsumptionIdentifierRequestBody;
import net.openrally.restaurant.response.body.ConsumptionIdentifierResponseBody;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("/consumption-identifier")
@Component
@Transactional
public class ConsumptionIdentifierResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ConsumptionIdentifierDAO consumptionIdentifierDAO;

	public static final String PATH = "consumption-identifier";

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response post(String requestBody,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, ForbiddenException, URISyntaxException,
			UnauthorizedException {

		User user = getRequestUser(loginToken);

		ConsumptionIdentifierRequestBody consumptionIdentifierRequestBody = retrieveConsumptionIdentifierRequestBody(requestBody);

		logger.debug("Creating new consumption identifier");

		ConsumptionIdentifier consumptionIdentifier = new ConsumptionIdentifier();

		consumptionIdentifier.setCompany(user.getCompany());
		consumptionIdentifier.setDescription(consumptionIdentifierRequestBody
				.getDescription());
		consumptionIdentifier.setIdentifier(consumptionIdentifierRequestBody
				.getIdentifier());

		logger.debug("Saving new consumption identifier");

		try {
			consumptionIdentifierDAO.save(consumptionIdentifier);
			consumptionIdentifierDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ consumptionIdentifier.getConsumptionIdentifierId());

		logger.debug("Finished processing request successfully");

		return Response.created(locationURI).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response getList(
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		User user = getRequestUser(loginToken);

		List<ConsumptionIdentifier> entityList = consumptionIdentifierDAO
				.getAllByCompanyId(user.getCompany().getCompanyId());

		List<ConsumptionIdentifierResponseBody> entityResponseBodyList = new LinkedList<ConsumptionIdentifierResponseBody>();

		for (ConsumptionIdentifier entity : entityList) {
			ConsumptionIdentifierResponseBody entityResponseBody = new ConsumptionIdentifierResponseBody(
					entity);
			entityResponseBodyList.add(entityResponseBody);
		}

		return Response.ok(gson.toJson(entityResponseBodyList)).build();
	}

	@GET
	@Path("/{entityId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response get(@PathParam("entityId") String entityIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		User user = getRequestUser(loginToken);

		ConsumptionIdentifier consumptionIdentifier = retrieveConsumptionIdentifier(entityIdString);

		if (!consumptionIdentifier.getCompany().getCompanyId().equals(user.getCompany().getCompanyId())) {
			throw new ForbiddenException();
		}

		ConsumptionIdentifierResponseBody consumptionIdentifierResponseBody = new ConsumptionIdentifierResponseBody(
				consumptionIdentifier);

		return Response.ok(gson.toJson(consumptionIdentifierResponseBody))
				.build();
	}

	@DELETE
	@Path("/{entityId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = ConflictException.class)
	public Response delete(@PathParam("entityId") String entityIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			ConflictException, UnauthorizedException {

		User user = getRequestUser(loginToken);

		ConsumptionIdentifier consumptionIdentifier = retrieveConsumptionIdentifier(entityIdString);

		if (!consumptionIdentifier.getCompany().getCompanyId().equals(user.getCompany().getCompanyId())) {
			throw new ForbiddenException();
		}

		try {
			consumptionIdentifierDAO.delete(consumptionIdentifier);
			consumptionIdentifierDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new ConflictException(
					MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES);
		}

		return Response.noContent().build();
	}

	@PUT
	@Path("/{entityId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response put(String requestBody,
			@PathParam("entityId") String entityIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			URISyntaxException, UnauthorizedException {

		User user = getRequestUser(loginToken);

		ConsumptionIdentifier consumptionIdentifier = retrieveConsumptionIdentifier(entityIdString);

		if (!consumptionIdentifier.getCompany().getCompanyId().equals(user.getCompany().getCompanyId())) {
			throw new ForbiddenException();
		}

		ConsumptionIdentifierRequestBody consumptionIdentifierRequestBody = retrieveConsumptionIdentifierRequestBody(requestBody);

		consumptionIdentifier.setIdentifier(consumptionIdentifierRequestBody
				.getIdentifier());
		consumptionIdentifier.setDescription(consumptionIdentifierRequestBody
				.getDescription());

		try {
			consumptionIdentifierDAO.update(consumptionIdentifier);
			consumptionIdentifierDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(
					MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES_OR_DUPLICATE);
		}

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ consumptionIdentifier.getConsumptionIdentifierId());

		logger.debug("Finished processing request successfully");

		return Response.ok().contentLocation(locationURI).build();
	}

	private ConsumptionIdentifier retrieveConsumptionIdentifier(
			String consumptionIdentifierIdString) throws BadRequestException,
			NotFoundException {
		Long consumptionIdentifierId;

		logger.debug("Retrieving consumption identifier from id parameter");
		logger.debug("Converting consumption identifier id from string to Long");

		try {
			consumptionIdentifierId = Long
					.parseLong(consumptionIdentifierIdString);
		} catch (NumberFormatException e) {
			logger.debug("Malformed consumption identifier id: "
					+ consumptionIdentifierIdString);
			throw new BadRequestException(MSG_INVALID_ENTITY_IDENTIFIER);
		}

		ConsumptionIdentifier consumptionIdentifier = consumptionIdentifierDAO
				.get(consumptionIdentifierId);

		if (null == consumptionIdentifier) {
			throw new NotFoundException();
		}

		return consumptionIdentifier;
	}

	private ConsumptionIdentifierRequestBody retrieveConsumptionIdentifierRequestBody(
			String requestBodyString) throws BadRequestException {
		ConsumptionIdentifierRequestBody consumptionIdentifierRequestBody;

		try {
			consumptionIdentifierRequestBody = gson.fromJson(requestBodyString,
					ConsumptionIdentifierRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == consumptionIdentifierRequestBody) {
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}

		consumptionIdentifierRequestBody.validate();

		return consumptionIdentifierRequestBody;
	}
}
