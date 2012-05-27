package net.openrally.restaurant.core.response.body;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;

public class UserResponseBody {

	private Long userId;
	private String login;
	private List<Long> roles;

	public UserResponseBody(User user) {
		this.userId = user.getUserId();
		this.login = user.getLogin();

		Set<Role> roleList = user.getRoles();
		roles = new LinkedList<Long>();

		if (null != roleList) {
			for (Role role : roleList) {
				roles.add(role.getRoleId());
			}
		}
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public List<Long> getRoles() {
		return roles;
	}

	public void setRoles(List<Long> roles) {
		this.roles = roles;
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof UserResponseBody))
			return false;
		UserResponseBody otherRoleResponseBody = (UserResponseBody) other;
		if (null == otherRoleResponseBody.getUserId() || !otherRoleResponseBody.getUserId().equals(this.getUserId())) {
			return false;
		}
		if (!StringUtils.equals(otherRoleResponseBody.getLogin(),
				this.getLogin())) {
			return false;
		}

		if (null == roles && null != otherRoleResponseBody.roles) {
			return false;
		}

		if (null == otherRoleResponseBody.roles && null != roles) {
			return false;
		}

		if (!roles.containsAll(otherRoleResponseBody.roles)) {
			return false;
		}
		if (!otherRoleResponseBody.roles.containsAll(roles)) {
			return false;
		}
		return true;
	}
}
