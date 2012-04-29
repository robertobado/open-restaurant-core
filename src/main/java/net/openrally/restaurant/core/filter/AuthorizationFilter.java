package net.openrally.restaurant.core.filter;

import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.persistence.dao.LoginTokenDAO;
import net.openrally.restaurant.core.persistence.entity.LoginToken;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Component
public class AuthorizationFilter implements ContainerRequestFilter {
	
	@Autowired
	protected LoginTokenDAO loginTokenDAO;

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		
		// Login is the only resource accessible without a login token
		if(!request.getPath().equals("login")){
			String token = request.getHeaderValue(BaseResource.LOGIN_TOKEN_HEADER_PARAMETER_NAME);
			
			if(null == token || StringUtils.isBlank(token)){
				throw new UnauthorizedException("Missing or blank authorization token");
			}
			
			LoginToken loginToken = loginTokenDAO.loadByToken(token);
			
			if(null == loginToken){
				throw new UnauthorizedException("Invalid authorization token");
			}
			
			if(loginToken.getExpirationTime() < (System.currentTimeMillis()/1000)){
				throw new UnauthorizedException("Expired authorization token");
			}
			
		}
		
		return request;
	}

}
