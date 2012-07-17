package net.openrally.restaurant.core.security;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.openrally.restaurant.core.exception.ForbiddenException;
import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.persistence.dao.LoginTokenDAO;
import net.openrally.restaurant.core.persistence.dao.PermissionDAO;
import net.openrally.restaurant.core.persistence.dao.RoleDAO;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("authorizationValidator")
@Transactional
public class AuthorizationValidator {

	private static final String DELETE = "DELETE";

	private static final String PUT = "PUT";

	private static final String POST = "POST";

	private static final String GET = "GET";

	@Autowired
	protected LoginTokenDAO loginTokenDAO;

	@Autowired
	protected PermissionDAO permissionDAO;

	@Autowired
	protected RoleDAO roleDAO;

	public void validateAccess(String loginTokenString, String path,
			String method) throws ForbiddenException, UnauthorizedException {
		if(path.startsWith("/")){
			path = path.replaceFirst("/", "");
		}
		
		// Login and company are the only resource accessible without a login token
		if (!path.equals("login") && !path.equals("company")) {

			if (null == loginTokenString
					|| StringUtils.isBlank(loginTokenString)) {
				throw new UnauthorizedException(
						"Missing or blank authorization token");
			}

			LoginToken loginToken = loginTokenDAO.loadByToken(loginTokenString);

			if (null == loginToken) {
				throw new ForbiddenException("Invalid authorization token");
			}

			if (loginToken.getExpirationTime() < (System.currentTimeMillis() / 1000)) {
				throw new ForbiddenException("Expired authorization token");
			}
			
			if(!path.startsWith(BaseResource.SLASH)){
				path = BaseResource.SLASH + path;
			}

			if (path.indexOf('?') >= 0) {
				path = path.substring(0, path.indexOf('?'));
			}

			while (path.length() > 1 && path.endsWith(BaseResource.SLASH)) {
				path = path.substring(0, path.length() - 1);
			}

			Set<Role> roleSet = loginToken.getUser().getRoles();

			List<Permission> permissionsList = new LinkedList<Permission>();

			for (Role role : roleSet) {
				permissionsList.addAll(permissionDAO.listByRoleId(role
						.getRoleId()));
			}

			while (path.length() > 0) {
				for (Permission permission : permissionsList) {
					if (StringUtils.equals(path, permission.getPath())) {
						if (StringUtils.equals(method, GET)) {
							if(permission.isAllowGet()){
								return;
							}
							else{
								throw new ForbiddenException();
							}
						}
						if (StringUtils.equals(method, POST)) {
							if(permission.isAllowPost()){
								return;
							}
							else{
								throw new ForbiddenException();
							}
						}
						if (StringUtils.equals(method, PUT)) {
							if(permission.isAllowPut()){
								return;
							}
							else{
								throw new ForbiddenException();
							}
						}
						if (StringUtils.equals(method, DELETE)) {
							if(permission.isAllowDelete()){
								return;
							}
							else{
								throw new ForbiddenException();
							}
						}

					}
				}
				if (path.endsWith(BaseResource.SLASH)) {
					path = path.substring(0,
							path.lastIndexOf(BaseResource.SLASH));
				} else {
					path = path.substring(0, path.length() - 1);
				}
			}

			throw new ForbiddenException();
		}
	}
}
