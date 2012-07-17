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
import net.openrally.restaurant.core.persistence.dao.TaxDAO;
import net.openrally.restaurant.core.persistence.entity.Tax;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.request.body.TaxRequestBody;
import net.openrally.restaurant.response.body.TaxResponseBody;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("/tax")
@Component
@Transactional
public class TaxResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String PATH = "tax";
	
	@Autowired
	private TaxDAO taxDAO;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response post(String requestBody,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, ForbiddenException, URISyntaxException,
			UnauthorizedException, ConflictException {

		User user = getRequestUser(loginToken);

		TaxRequestBody entityRequestBody = retrieveEntityRequestBody(requestBody);

		logger.debug("Creating new tax");

		Tax tax = new Tax();
		
		tax.setCompany(user.getCompany());
		tax.setName(entityRequestBody.getName());
		tax.setDescription(entityRequestBody.getDescription());
		tax.setAmount(entityRequestBody.getAmount());
		tax.setPercentage(entityRequestBody.getPercentage());

		logger.debug("Saving new tax");

		try {
			taxDAO.save(tax);
			taxDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ tax.getTaxId());

		logger.debug("Finished processing request successfully");

		return Response.created(locationURI).build();
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

		Tax entity = retrieveEntity(entityIdString);

		if (!user.getCompany().getCompanyId().equals(entity.getCompany().getCompanyId())) {
			throw new ForbiddenException();
		}

		TaxResponseBody entityResponseBody = new TaxResponseBody(entity);

		return Response.ok(gson.toJson(entityResponseBody)).build();
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

		List<Tax> entityList = taxDAO.getAllByCompanyId(user
				.getCompany().getCompanyId());

		List<TaxResponseBody> entityResponseBodyList = new LinkedList<TaxResponseBody>();

		for (Tax entityInstance : entityList) {
			TaxResponseBody entityResponseBody = new TaxResponseBody(entityInstance);
			entityResponseBodyList.add(entityResponseBody);
		}

		return Response.ok(gson.toJson(entityResponseBodyList)).build();
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

		Tax entity = retrieveEntity(entityIdString);

		if (!user.getCompany().getCompanyId().equals(entity.getCompany()
				.getCompanyId())) {
			throw new ForbiddenException();
		}

		try {
			taxDAO.delete(entity);
			taxDAO.flush();
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
			URISyntaxException, UnauthorizedException, ConflictException {

		User user = getRequestUser(loginToken);

		Tax entity = retrieveEntity(entityIdString);

		if (!user.getCompany().getCompanyId().equals(entity.getCompany()
				.getCompanyId())) {
			throw new ForbiddenException();
		}

		TaxRequestBody entityRequestBody = retrieveEntityRequestBody(requestBody);
		
		entity.setName(entityRequestBody.getName());
		entity.setDescription(entityRequestBody.getDescription());
		entity.setAmount(entityRequestBody.getAmount());
		entity.setPercentage(entityRequestBody.getPercentage());

		logger.debug("Updating tax");

		try {
			taxDAO.update(entity);
			taxDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ entity.getTaxId());

		logger.debug("Finished processing request successfully");

		return Response.ok().contentLocation(locationURI).build();
	}
	
	private TaxRequestBody retrieveEntityRequestBody(String requestBodyString)
			throws BadRequestException {
		TaxRequestBody entityRequestBody;

		try {
			entityRequestBody = gson.fromJson(requestBodyString,
					TaxRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == entityRequestBody) {
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}

		entityRequestBody.validate();

		return entityRequestBody;
	}
	
	private Tax retrieveEntity(String entityIdString)
			throws BadRequestException, NotFoundException {
		Long entityId;

		logger.debug("Retrieving tax from id parameter");
		logger.debug("Converting tax id from string to Long");

		try {
			entityId = Long.parseLong(entityIdString);
		} catch (NumberFormatException e) {
			logger.debug("Malformed tax id: " + entityIdString);
			throw new BadRequestException(MSG_INVALID_ENTITY_IDENTIFIER);
		}

		Tax entity = taxDAO.get(entityId);

		if (null == entity) {
			throw new NotFoundException();
		}

		return entity;
	}
}
