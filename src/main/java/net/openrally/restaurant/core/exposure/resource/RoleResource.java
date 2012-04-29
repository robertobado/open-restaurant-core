package net.openrally.restaurant.core.exposure.resource;

import java.net.URI;
import java.net.URISyntaxException;

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
import net.openrally.restaurant.core.exception.ForbiddenException;
import net.openrally.restaurant.core.exception.NotFoundException;
import net.openrally.restaurant.core.persistence.dao.RoleDAO;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.RoleRequestBody;
import net.openrally.restaurant.core.response.body.RoleResponseBody;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;

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
			@HeaderParam(LOGIN_TOKEN_HEADER_PARAMETER_NAME) String loginToken)
			throws BadRequestException, URISyntaxException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role POST request: "
					+ requestBody);
		}

		logger.debug("Retrieving request user from login token");
		User user = getRequestUser(loginToken);

		RoleRequestBody roleRequestBody = retrieveRoleRequestBody(requestBody);

		logger.debug("Validating request");

		roleRequestBody.validate();

		logger.debug("Creating new role");

		Role role = new Role();

		role.setCompany(user.getCompany());
		role.setName(roleRequestBody.getName());
		role.setDescription(roleRequestBody.getDescription());

		logger.debug("Saving new role");

		try {
			roleDAO.save(role);
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
	@Path("/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response get(@PathParam("roleId") String roleIdString,
			@HeaderParam(LOGIN_TOKEN_HEADER_PARAMETER_NAME) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role GET request");
		}

		logger.debug("Retrieving role from id parameter");
		Role role = retrieveRole(roleIdString);

		logger.debug("Retrieving request user from login token");
		User user = getRequestUser(loginToken);

		if (role.getCompany().getCompanyId() != user.getCompany()
				.getCompanyId()) {
			throw new ForbiddenException();
		}

		RoleResponseBody roleResponseBody = new RoleResponseBody();

		roleResponseBody.setRoleId(role.getRoleId());
		roleResponseBody.setName(role.getName());
		roleResponseBody.setDescription(role.getDescription());

		return Response.ok(gson.toJson(roleResponseBody)).build();

	}

	@DELETE
	@Path("/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("roleId") String roleIdString,
			@HeaderParam(LOGIN_TOKEN_HEADER_PARAMETER_NAME) String loginToken) throws BadRequestException, NotFoundException, ForbiddenException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role DELETE request");
		}

		logger.debug("Retrieving role from id parameter");
		Role role = retrieveRole(roleIdString);

		logger.debug("Retrieving request user from login token");
		User user = getRequestUser(loginToken);


		if (role.getCompany().getCompanyId() != user.getCompany()
				.getCompanyId()) {
			throw new ForbiddenException();
		}
		
		roleDAO.delete(role);
		
		return Response.noContent().build();

	}

	@PUT
	@Path("/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(String requestBody,
			@PathParam("roleId") String roleIdString,
			@HeaderParam(LOGIN_TOKEN_HEADER_PARAMETER_NAME) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role PUT request: "
					+ requestBody);
		}

		logger.debug("Retrieving role from id parameter");
		Role role = retrieveRole(roleIdString);

		logger.debug("Retrieving request user from login token");
		User user = getRequestUser(loginToken);

		if (role.getCompany().getCompanyId() != user.getCompany()
				.getCompanyId()) {
			throw new ForbiddenException();
		}

		RoleRequestBody roleRequestBody = retrieveRoleRequestBody(requestBody);
		
		role.setName(roleRequestBody.getName());
		role.setDescription(roleRequestBody.getDescription());
		
		roleDAO.update(role);

		return Response.ok().build();

	}
	
	private Role retrieveRole(String roleIdString) throws BadRequestException, NotFoundException{
		long roleId;

		logger.debug("Converting role id from string to long");
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
	
	private RoleRequestBody retrieveRoleRequestBody(String requestBodyString) throws BadRequestException{
		RoleRequestBody roleRequestBody;

		try {
			roleRequestBody = gson.fromJson(requestBodyString, RoleRequestBody.class);
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
