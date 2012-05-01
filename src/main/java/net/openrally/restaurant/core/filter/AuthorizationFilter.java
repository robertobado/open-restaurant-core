package net.openrally.restaurant.core.filter;

import net.openrally.restaurant.core.security.AuthorizationValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Component
public class AuthorizationFilter implements ContainerRequestFilter {

	@Autowired
	protected AuthorizationValidator authorizationValidator;

	@Override
	public ContainerRequest filter(ContainerRequest request) {

		String loginToken = request.getHeaderValue(ContainerRequest.AUTHORIZATION);
		String path = request.getPath();
		String method = request.getMethod();
		
		authorizationValidator.validateAccess(loginToken, path, method);

		return request;
	}

}
