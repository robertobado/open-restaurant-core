package net.openrally.restaurant.core.exposure.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import net.openrally.restaurant.core.persistence.dao.ConfigurationDAO;
import net.openrally.restaurant.core.persistence.dao.RoleDAO;
import net.openrally.restaurant.core.persistence.dao.UserDAO;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.UserRequestBody;
import net.openrally.restaurant.core.response.body.UserListResponseBody;
import net.openrally.restaurant.core.response.body.UserResponseBody;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("/user")
@Component
@Transactional
public class UserResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public final static String PATH = "user";

	@Autowired
	protected ConfigurationDAO configurationDAO;

	@Autowired
	protected RoleDAO roleDAO;

	@Autowired
	protected UserDAO userDAO;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response post(String requestBody,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, URISyntaxException,
			UnauthorizedException, NotFoundException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a user POST request: "
					+ requestBody);
		}

		User user = getRequestUser(loginToken);

		UserRequestBody userRequestBody = retrieveUserRequestBody(requestBody);

		logger.debug("Creating new user");

		Configuration configuration = configurationDAO.loadByCompanyId(user
				.getCompany().getCompanyId());

		String hashSalt = configuration.getHashSalt();

		User newUser = new User();
		newUser.setCompany(user.getCompany());
		newUser.setLogin(userRequestBody.getLogin());
		newUser.setPasswordHash(User.generatePasswordHash(
				userRequestBody.getPassword(), hashSalt));

		addUserRoles(newUser, userRequestBody);

		logger.debug("Saving new user");

		try {
			userDAO.save(newUser);
			userDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI roleLocationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ newUser.getUserId());

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
			logger.debug("Starting to process a user GET request");
		}

		User user = getRequestUser(loginToken);

		List<User> userList = userDAO.getAllByCompanyId(user.getCompany()
				.getCompanyId());

		List<UserResponseBody> responseUserList = new LinkedList<UserResponseBody>();

		for (User listUser : userList) {
			UserResponseBody userResponseBody = new UserResponseBody(listUser);
			responseUserList.add(userResponseBody);
		}

		UserListResponseBody roleListResponseBody = new UserListResponseBody();
		roleListResponseBody.setList(responseUserList);

		return Response.ok(gson.toJson(roleListResponseBody)).build();
	}

	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response get(@PathParam("userId") String userIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a user GET request");
		}

		User user = getRequestUser(loginToken);

		User queryUser = retrieveUser(userIdString);

		if (!queryUser.getCompany().getCompanyId().equals(user
				.getCompany().getCompanyId())) {
			throw new ForbiddenException();
		}

		UserResponseBody userResponseBody = new UserResponseBody(queryUser);

		return Response.ok(gson.toJson(userResponseBody)).build();
	}

	@DELETE
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = ConflictException.class)
	public Response delete(@PathParam("userId") String userIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			ConflictException, UnauthorizedException {
		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a user DELETE request");
		}

		User deleteUser = retrieveUser(userIdString);

		User user = getRequestUser(loginToken);

		if (!deleteUser.getCompany().getCompanyId().equals(user
				.getCompany().getCompanyId())) {
			throw new ForbiddenException();
		}

		try {
			userDAO.delete(deleteUser);
			userDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new ConflictException(
					MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES);
		}

		return Response.noContent().build();
	}

	@PUT
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response put(String requestBody,
			@PathParam("userId") String userIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			URISyntaxException, UnauthorizedException, ConflictException {
		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a role PUT request: "
					+ requestBody);
		}

		User putUser = retrieveUser(userIdString);

		User user = getRequestUser(loginToken);

		if (!putUser.getCompany().getCompanyId().equals(user.getCompany()
				.getCompanyId())) {
			throw new ForbiddenException();
		}

		UserRequestBody userRequestBody = retrieveUserRequestBody(requestBody);

		Configuration configuration = configurationDAO.loadByCompanyId(user
				.getCompany().getCompanyId());

		String hashSalt = configuration.getHashSalt();

		putUser.setLogin(userRequestBody.getLogin());
		putUser.setPasswordHash(User.generatePasswordHash(
				userRequestBody.getPassword(), hashSalt));
		addUserRoles(putUser, userRequestBody);

		try {
			userDAO.update(putUser);
			userDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new ConflictException(
					MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES_OR_DUPLICATE);
		}

		URI roleLocationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ putUser.getUserId());

		logger.debug("Finished processing request successfully");

		return Response.ok().contentLocation(roleLocationURI).build();
	}

	private UserRequestBody retrieveUserRequestBody(String requestBodyString)
			throws BadRequestException {
		UserRequestBody userRequestBody;

		try {
			userRequestBody = gson.fromJson(requestBodyString,
					UserRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == userRequestBody) {
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}

		userRequestBody.validate();

		return userRequestBody;
	}

	private User retrieveUser(String userIdString) throws BadRequestException,
			NotFoundException {
		Long userId;

		logger.debug("Retrieving user from id parameter");
		logger.debug("Converting user id from string to Long");

		try {
			userId = Long.parseLong(userIdString);
		} catch (NumberFormatException e) {
			logger.debug("Malformed user id: " + userIdString);
			throw new BadRequestException(MSG_INVALID_ENTITY_IDENTIFIER);
		}

		User user = userDAO.get(userId);

		if (null == user) {
			throw new NotFoundException();
		}

		return user;
	}

	private void addUserRoles(User user, UserRequestBody userRequestBody)
			throws NotFoundException {
		Set<Role> roles = new HashSet<Role>();

		List<Long> roleIdList = userRequestBody.getRoles();

		if (null != roleIdList) {
			for (Long roleId : roleIdList) {

				Role role = roleDAO.get(roleId);

				if (null == role) {
					throw new NotFoundException("Role not found");
				}

				if (!role.getCompany().getCompanyId().equals(user
						.getCompany().getCompanyId())) {
					throw new ForbiddenException();
				}

				roles.add(role);
			}
		}

		user.setRoles(roles);
	}

}
