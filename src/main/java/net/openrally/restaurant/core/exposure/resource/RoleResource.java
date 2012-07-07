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
import net.openrally.restaurant.core.persistence.dao.RoleDAO;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.request.body.RoleRequestBody;
import net.openrally.restaurant.response.body.RoleResponseBody;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("/role")
@Component
public class RoleResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public final static String PATH = "role";

	@Autowired
	protected RoleDAO roleDAO;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response post(String requestBody,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, URISyntaxException,
			UnauthorizedException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role POST request: "
					+ requestBody);
		}

		User user = getRequestUser(loginToken);

		RoleRequestBody roleRequestBody = retrieveRoleRequestBody(requestBody);

		logger.debug("Creating new role");

		Role role = new Role();

		role.setCompany(user.getCompany());
		role.setName(roleRequestBody.getName());
		role.setDescription(roleRequestBody.getDescription());

		logger.debug("Saving new role");

		try {
			roleDAO.save(role);
			roleDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI roleLocationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ role.getRoleId());

		logger.debug("Finished processing request successfully");

		return Response.created(roleLocationURI).build();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response getList(
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role GET request");
		}

		User user = getRequestUser(loginToken);

		List<Role> roleList = roleDAO.listAllByCompanyId(user.getCompany()
				.getCompanyId());

		List<RoleResponseBody> responseRoleList = new LinkedList<RoleResponseBody>();

		for (Role role : roleList) {
			RoleResponseBody roleResponseBody = new RoleResponseBody(role);
			responseRoleList.add(roleResponseBody);
		}

		return Response.ok(gson.toJson(responseRoleList)).build();

	}

	@GET
	@Path("/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response get(@PathParam("roleId") String roleIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role GET request");
		}

		User user = getRequestUser(loginToken);

		Role role = retrieveRole(roleIdString);

		if (!role.getCompany().getCompanyId().equals(user.getCompany().getCompanyId())) {
			throw new ForbiddenException();
		}

		RoleResponseBody roleResponseBody = new RoleResponseBody(role);

		return Response.ok(gson.toJson(roleResponseBody)).build();

	}

	@DELETE
	@Path("/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = ConflictException.class)
	public Response delete(@PathParam("roleId") String roleIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			ConflictException, UnauthorizedException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role DELETE request");
		}

		Role role = retrieveRole(roleIdString);

		User user = getRequestUser(loginToken);

		if (!role.getCompany().getCompanyId().equals(user.getCompany()
				.getCompanyId())) {
			throw new ForbiddenException();
		}

		try {
			roleDAO.delete(role);
			roleDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new ConflictException(
					MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES);
		}

		return Response.noContent().build();

	}

	@PUT
	@Path("/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response put(String requestBody,
			@PathParam("roleId") String roleIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			URISyntaxException, UnauthorizedException, ConflictException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role PUT request: "
					+ requestBody);
		}

		Role role = retrieveRole(roleIdString);

		User user = getRequestUser(loginToken);

		if (!role.getCompany().getCompanyId().equals(user.getCompany()
				.getCompanyId())) {
			throw new ForbiddenException();
		}

		RoleRequestBody roleRequestBody = retrieveRoleRequestBody(requestBody);

		role.setName(roleRequestBody.getName());
		role.setDescription(roleRequestBody.getDescription());

		try {
			roleDAO.update(role);
			roleDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new ConflictException(
					MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES_OR_DUPLICATE);
		}

		URI roleLocationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ role.getRoleId());

		logger.debug("Finished processing request successfully");

		return Response.ok().contentLocation(roleLocationURI).build();

	}

	private Role retrieveRole(String roleIdString) throws BadRequestException,
			NotFoundException {
		Long roleId;

		logger.debug("Retrieving role from id parameter");
		logger.debug("Converting role id from string to Long");

		try {
			roleId = Long.parseLong(roleIdString);
		} catch (NumberFormatException e) {
			logger.debug("Malformed role id: " + roleIdString);
			throw new BadRequestException(MSG_INVALID_ENTITY_IDENTIFIER);
		}

		Role role = roleDAO.get(roleId);

		if (null == role) {
			throw new NotFoundException();
		}

		return role;
	}

	private RoleRequestBody retrieveRoleRequestBody(String requestBodyString)
			throws BadRequestException {
		RoleRequestBody roleRequestBody;

		try {
			roleRequestBody = gson.fromJson(requestBodyString,
					RoleRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == roleRequestBody) {
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}

		roleRequestBody.validate();

		return roleRequestBody;
	}

}
