package net.openrally.restaurant.core.exposure.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.persistence.dao.LoginTokenDAO;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;

@Component
@Transactional
@Singleton
@Produces("application/json")
@Consumes("application/json")
public abstract class BaseResource {
	
	// Messages
	protected static final String MSG_INVALID_JSON_AS_REQUEST_BODY = "Invalid JSON as request body";
	protected static final String MSG_REQUEST_BODY_MISSING_OR_BLANK = "Request body missing or blank";
	protected static final String MSG_DUPLICATE_ENTITY = "Duplicate entity";
	protected static final String MSG_INVALID_ENTITY_IDENTIFIER = "Invalid entity identifier";

	// Header parameters
	public static final String LOGIN_TOKEN_HEADER_PARAMETER_NAME = "login-token";
	public static final String LOCATION_HEADER_PARAMETER_NAME = "Location";
	
	public static final String HTTP = "http";
	public static final String COLON = ":";
	public static final String SLASH = "/";
	public static final String SERVER_HOSTNAME = "localhost";
	public static final int PORT_NUMBER = 8282;
	public static final String CONTEXT_PATH = "open-restaurant-core";
	public static final String CONTENT_TYPE = "content-type";

	protected Gson gson = new Gson();
	
	@Autowired
	protected LoginTokenDAO loginTokenDAO;

	protected User getRequestUser(String loginTokenString) {
		
		LoginToken loginToken = loginTokenDAO.loadByToken(loginTokenString);
		
		User user = loginToken.getUser();

		if (null == user){
			throw new UnauthorizedException("Invalid authorization token");
		}
		
		return user;
	}
	
	public static final String getServerBasePath() {
		return HTTP + COLON + SLASH + SLASH + SERVER_HOSTNAME + COLON
				+ PORT_NUMBER + SLASH + CONTEXT_PATH;
	}
}
