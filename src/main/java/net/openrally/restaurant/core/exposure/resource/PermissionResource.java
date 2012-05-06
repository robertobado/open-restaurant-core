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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.exception.ConflictException;
import net.openrally.restaurant.core.exception.ForbiddenException;
import net.openrally.restaurant.core.exception.NotFoundException;
import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.persistence.dao.PermissionDAO;
import net.openrally.restaurant.core.persistence.dao.RoleDAO;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.PermissionRequestBody;
import net.openrally.restaurant.core.response.body.PermissionListResponseBody;
import net.openrally.restaurant.core.response.body.PermissionResponseBody;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("/permission")
@Component
public class PermissionResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public final static String PATH = "permission";

	@Autowired
	protected PermissionDAO permissionDAO;

	@Autowired
	protected RoleDAO roleDAO;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response post(String requestBody,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, ForbiddenException, URISyntaxException, UnauthorizedException {

		User user = getRequestUser(loginToken);

		PermissionRequestBody permissionRequestBody = retrievePermissionRequestBody(requestBody);

		if (!permissionRequestBody.getPath().startsWith("/")) {
			throw new BadRequestException("Invalid path parameter");
		}

		Role role = roleDAO.get(permissionRequestBody.getRoleId());

		if (role.getCompany().getCompanyId() != user.getCompany()
				.getCompanyId()) {
			throw new ForbiddenException();
		}

		logger.debug("Creating new permission");

		// Cleanup path from trailing slashes
		String path = permissionRequestBody.getPath();

		while (path.length() > 1 && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath(path);
		permission.setAllowPost(permissionRequestBody.isAllowPost());
		permission.setAllowPut(permissionRequestBody.isAllowPut());
		permission.setAllowGet(permissionRequestBody.isAllowGet());
		permission.setAllowDelete(permissionRequestBody.isAllowDelete());

		logger.debug("Saving new permission");

		try {
			permissionDAO.save(permission);
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ permission.getPermissionId());

		logger.debug("Finished processing request successfully");

		return Response.created(locationURI).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response getList(@QueryParam("roleId") String roleIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken) throws BadRequestException, NotFoundException, ForbiddenException, UnauthorizedException {

		User user = getRequestUser(loginToken);
		
		Role role = retrieveRole(roleIdString);
		
		if (role.getCompany().getCompanyId() != user
				.getCompany().getCompanyId()) {
			throw new ForbiddenException();
		}
		
		List<Permission> permissionList = permissionDAO.listByRoleId(role.getRoleId());
		
		List<PermissionResponseBody> permissionResponseList = new LinkedList<PermissionResponseBody>();
		
		for(Permission permission : permissionList){
			PermissionResponseBody permissionResponseBody = new PermissionResponseBody(permission);
			permissionResponseList.add(permissionResponseBody);
		}
		
		PermissionListResponseBody permissionListResponseBody = new PermissionListResponseBody();
		
		permissionListResponseBody.setList(permissionResponseList);

		return Response.ok(gson.toJson(permissionListResponseBody)).build();
	}

	@GET
	@Path("/{permissionId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response get(@PathParam("permissionId") String permissionIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException, UnauthorizedException {

		User user = getRequestUser(loginToken);

		Permission permission = retrievePermission(permissionIdString);

		if (permission.getRole().getCompany().getCompanyId() != user
				.getCompany().getCompanyId()) {
			throw new ForbiddenException();
		}

		PermissionResponseBody permissionResponseBody = new PermissionResponseBody(
				permission);

		return Response.ok(gson.toJson(permissionResponseBody)).build();
	}

	@DELETE
	@Path("/{permissionId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = ConflictException.class)
	public Response delete(
			@PathParam("permissionId") String permissionIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken) throws BadRequestException, NotFoundException, ForbiddenException, ConflictException, UnauthorizedException {

		User user = getRequestUser(loginToken);
		
		Permission permission = retrievePermission(permissionIdString);
		
		if (permission.getRole().getCompany().getCompanyId() != user
				.getCompany().getCompanyId()) {
			throw new ForbiddenException();
		}
		
		try {
			permissionDAO.delete(permission);
			permissionDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new ConflictException(MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES);
		}

		return Response.noContent().build();
	}

	@PUT
	@Path("/{permissionId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response put(String requestBody,
			@PathParam("permissionId") String permissionIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException, URISyntaxException, UnauthorizedException {

		User user = getRequestUser(loginToken);

		Permission permission = retrievePermission(permissionIdString);

		if (permission.getRole().getCompany().getCompanyId() != user
				.getCompany().getCompanyId()) {
			throw new ForbiddenException();
		}

		PermissionRequestBody permissionRequestBody = retrievePermissionRequestBody(requestBody);

		if (!permissionRequestBody.getPath().startsWith("/")) {
			throw new BadRequestException("Invalid path parameter");
		}

		if (permission.getRole().getRoleId() != permissionRequestBody
				.getRoleId()) {
			Role role = roleDAO.get(permissionRequestBody.getRoleId());

			if (role.getCompany().getCompanyId() != user.getCompany()
					.getCompanyId()) {
				throw new ForbiddenException();
			}
			permission.setRole(role);
		}

		// Cleanup path from trailing slashes
		String path = permissionRequestBody.getPath();

		while (path.length() > 1 && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		permission.setPath(path);
		permission.setAllowPost(permissionRequestBody.isAllowPost());
		permission.setAllowPut(permissionRequestBody.isAllowPut());
		permission.setAllowGet(permissionRequestBody.isAllowGet());
		permission.setAllowDelete(permissionRequestBody.isAllowDelete());
		
		try {
			permissionDAO.save(permission);
			permissionDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES_OR_DUPLICATE);
		}

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ permission.getPermissionId());

		logger.debug("Finished processing request successfully");

		return Response.ok().contentLocation(locationURI).build();
	}

	private Permission retrievePermission(String permissionIdString)
			throws BadRequestException, NotFoundException {
		long permissionId;

		logger.debug("Retrieving permission from id parameter");
		logger.debug("Converting permission id from string to long");

		try {
			permissionId = Long.parseLong(permissionIdString);
		} catch (NumberFormatException e) {
			logger.debug("Malformed permission id: " + permissionIdString);
			throw new BadRequestException(MSG_INVALID_ENTITY_IDENTIFIER);
		}

		Permission permission = permissionDAO.get(permissionId);

		if (null == permission) {
			throw new NotFoundException();
		}

		return permission;
	}
	
	private Role retrieveRole(String roleIdString)
			throws BadRequestException, NotFoundException {
		long roleId;

		logger.debug("Retrieving role from id parameter");
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

	private PermissionRequestBody retrievePermissionRequestBody(
			String requestBodyString) throws BadRequestException {
		PermissionRequestBody permissionRequestBody;

		try {
			permissionRequestBody = gson.fromJson(requestBodyString,
					PermissionRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == permissionRequestBody) {
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}

		permissionRequestBody.validate();

		return permissionRequestBody;
	}

}
