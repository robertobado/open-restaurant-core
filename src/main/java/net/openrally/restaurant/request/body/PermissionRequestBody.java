package net.openrally.restaurant.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;
import net.openrally.restaurant.core.annotation.ParameterValuePositive;


public class PermissionRequestBody extends BaseRequestBody{
	
	@ParameterRequired
	@ParameterValuePositive
	private Long roleId;
	
	@ParameterRequired
	private String path;
	
	private boolean allowPost;
	
	private boolean allowPut;
	
	private boolean allowGet;
	
	private boolean allowDelete;
	
	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isAllowPost() {
		return allowPost;
	}

	public void setAllowPost(boolean allowPost) {
		this.allowPost = allowPost;
	}

	public boolean isAllowPut() {
		return allowPut;
	}

	public void setAllowPut(boolean allowPut) {
		this.allowPut = allowPut;
	}

	public boolean isAllowGet() {
		return allowGet;
	}

	public void setAllowGet(boolean allowGet) {
		this.allowGet = allowGet;
	}

	public boolean isAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	
}
