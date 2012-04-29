package net.openrally.restaurant.core.filter;

import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.persistence.dao.LoginTokenDAO;
import net.openrally.restaurant.core.persistence.dao.PermissionDAO;
import net.openrally.restaurant.core.persistence.dao.RoleDAO;
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

	@Autowired
	protected PermissionDAO permissionDAO;

	@Autowired
	protected RoleDAO roleDAO;

	@Override
	public ContainerRequest filter(ContainerRequest request) {

		// Login is the only resource accessible without a login token
		if (!request.getPath().equals("login")) {
			String token = request
					.getHeaderValue(BaseResource.LOGIN_TOKEN_HEADER_PARAMETER_NAME);

			if (null == token || StringUtils.isBlank(token)) {
				throw new UnauthorizedException(
						"Missing or blank authorization token");
			}

			LoginToken loginToken = loginTokenDAO.loadByToken(token);

			if (null == loginToken) {
				throw new UnauthorizedException("Invalid authorization token");
			}

			if (loginToken.getExpirationTime() < (System.currentTimeMillis() / 1000)) {
				throw new UnauthorizedException("Expired authorization token");
			}

//			String path = request.getPath();
//
//			if (path.indexOf('?') >= 0) {
//				path = path.substring(0, path.indexOf('?'));
//			}
//
//			while (path.length() > 1 && path.endsWith("/")) {
//				path = path.substring(0, path.length() - 1);
//			}
//
//			Set<Role> roleSet = loginToken.getUser().getRoles();
//
//			List<Permission> permissionsList = new LinkedList<Permission>();
//
//			for (Role role : roleSet) {
//				permissionsList.addAll(permissionDAO.listByRoleId(role
//						.getRoleId()));
//			}
//
//			while (path.length() > 0) {
//				for (Permission permission : permissionsList) {
//					if (StringUtils.equals(path, permission.getPath())) {
//						request.getMethod();
//
//					}
//				}
//			}
//
//			throw new UnauthorizedException();
		}

		return request;
	}

}
