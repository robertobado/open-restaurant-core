package net.openrally.restaurant.core.exposure.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.persistence.dao.ConfigurationDAO;
import net.openrally.restaurant.core.persistence.dao.UserDAO;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.LoginRequestBody;
import net.openrally.restaurant.core.response.body.LoginResponseBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;

@Path("/login")
@Component
@Transactional
public class LoginResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String PATH = "login";

	@Autowired
	private ConfigurationDAO configurationDAO;

	@Autowired
	private UserDAO userDAO;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(String requestBody) throws BadRequestException {

		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a login request" + requestBody);
		}
		
		LoginRequestBody loginRequestBody;
		
		try {
			loginRequestBody = gson.fromJson(requestBody,
					LoginRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == loginRequestBody) {
			logger.debug(MSG_REQUEST_BODY_MISSING_OR_BLANK);
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}
		
		loginRequestBody.validate();

		long companyId = loginRequestBody.getCompanyId();
		String login = loginRequestBody.getLogin();

		User user = userDAO.loadByCompanyIdAndLogin(companyId, login);

		if (null == user) {
			logger.debug("Login denied due to user not found");
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		String password = loginRequestBody.getPassword();

		Configuration configuration = configurationDAO
				.loadByCompanyId(companyId);

		if (!checkUserPassword(user, password, configuration.getHashSalt())) {
			logger.debug("Login denied due to password mismatch");
			return Response.status(Status.UNAUTHORIZED).build();
		}

		logger.debug("Login granted, generating token...");

		long tokenLifeTime = configuration.getLoginTokenLifeTime();

		LoginToken loginToken = new LoginToken();
		loginToken.setUser(user);
		loginToken.setExpirationTime(System.currentTimeMillis() / 1000
				+ tokenLifeTime);
		loginToken.generateToken();

		loginTokenDAO.save(loginToken);

		LoginResponseBody loginResponseBody = new LoginResponseBody();

		loginResponseBody.setToken(loginToken.getToken());
		
		if(logger.isDebugEnabled()){
		logger.debug("Token generated: " + loginToken.getToken());
		}

		return Response.ok(gson.toJson(loginResponseBody)).build();
	}

	boolean checkUserPassword(User user, String password, String salt) {

		String receivedPasswordHash = User.generatePasswordHash(password, salt);

		return user.getPasswordHash().equals(receivedPasswordHash);
	}

}
