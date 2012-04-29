package net.openrally.restaurant.core.exposure.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.request.body.ConsumptionIdentifierRequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;

@Path("/consumption-identifier")
@Component
@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConsumptionIdentifierResource extends BaseResource {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(String requestBody, @HeaderParam(LOGIN_TOKEN_HEADER_PARAMETER_NAME) String loginToken) throws BadRequestException {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Starting to process a post consumption-identifier request" + requestBody);
		}
		
//		checkToken(loginToken);
		
		ConsumptionIdentifierRequestBody consumptionIdentifierRequestBody;
		
		try {
			consumptionIdentifierRequestBody = gson.fromJson(requestBody,
					ConsumptionIdentifierRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException("Invalid JSON as request body");
		}

//		if (null == loginRequestBody) {
//			logger.debug("Login denied to empty body");
//			throw new ValidationException("Missing request body");
//		}
//
//		if (StringUtils.isEmpty(loginRequestBody.getLogin())) {
//			logger.debug("Login denied to empty login parameter");
//			throw new ValidationException("Missing request login parameter");
//		}
//
//		if (loginRequestBody.getCompanyId() == 0L) {
//			logger.debug("Login denied to empty companyId parameter");
//			throw new ValidationException("Missing request companyId parameter");
//		}
//
//		if (StringUtils.isEmpty(loginRequestBody.getPassword())) {
//			logger.debug("Login denied to empty password parameter");
//			throw new ValidationException("Missing request password parameter");
//		}
//
//		long companyId = loginRequestBody.getCompanyId();
//		String login = loginRequestBody.getLogin();
//		String password = loginRequestBody.getPassword();
//
//		User user = userDAO.loadByCompanyIdAndLogin(companyId, login);
//
//		if (null == user) {
//			logger.debug("Login denied due to user not found");
//			return Response.status(Status.UNAUTHORIZED).build();
//		}
//
//		Configuration configuration = configurationDAO
//				.loadByCompanyId(companyId);
//
//		if (!checkUserPassword(user, password, configuration.getHashSalt())) {
//			logger.debug("Login denied due to password mismatch");
//			return Response.status(Status.UNAUTHORIZED).build();
//		}
//
//		logger.debug("Login granted, generating token...");
//
//		long tokenLifeTime = configuration.getLoginTokenLifeTime();
//
//		LoginToken loginToken = new LoginToken();
//		loginToken.setUser(user);
//		loginToken.setExpirationTime(System.currentTimeMillis() / 1000
//				+ tokenLifeTime);
//		loginToken.generateToken();
//
//		loginTokenDAO.save(loginToken);
//
//		LoginResponseBody loginResponseBody = new LoginResponseBody();
//
//		loginResponseBody.setToken(loginToken.getToken());
//		
//		if(logger.isDebugEnabled()){
//		logger.debug("Token generated: " + loginToken.getToken());
//		}
//
//		return Response.ok(gson.toJson(loginResponseBody)).build();
		return Response.ok().build();
	}
}
