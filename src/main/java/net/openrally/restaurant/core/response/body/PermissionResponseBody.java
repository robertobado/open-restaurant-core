package net.openrally.restaurant.core.response.body;

import org.apache.commons.lang.StringUtils;

import net.openrally.restaurant.core.persistence.entity.Permission;


public class PermissionResponseBody {
	
	private long permissionId;
	
	private String path;
	
	private long roleId;
	
	private boolean allowPost;
	
	private boolean allowPut;
	
	private boolean allowGet;
	
	private boolean allowDelete;
	
	public PermissionResponseBody(){
	}
	
	public PermissionResponseBody(Permission permission){
		this.permissionId = permission.getPermissionId();
		this.path = permission.getPath();
		this.roleId = permission.getRole().getRoleId();
		
		this.allowPost = permission.isAllowPost();
		this.allowPut = permission.isAllowPut();
		this.allowGet = permission.isAllowGet();
		this.allowDelete = permission.isAllowDelete();
	}

	public long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(long permissionId) {
		this.permissionId = permissionId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
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
	
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof PermissionResponseBody)) return false;
	    PermissionResponseBody otherRoleResponseBody = (PermissionResponseBody)other;
	    
	    if(otherRoleResponseBody.getPermissionId() != this.getPermissionId()){
	    	return false;
	    }
	    if(otherRoleResponseBody.getRoleId() != this.getRoleId()){
	    	return false;
	    }
	    if(!StringUtils.equals(otherRoleResponseBody.getPath(), this.getPath())){
	    	return false;
	    }
	    if(otherRoleResponseBody.isAllowPost() != this.isAllowPost()){
	    	return false;
	    }
	    if(otherRoleResponseBody.isAllowPut() != this.isAllowPut()){
	    	return false;
	    }
	    if(otherRoleResponseBody.isAllowGet() != this.isAllowGet()){
	    	return false;
	    }
	    if(otherRoleResponseBody.isAllowDelete() != this.isAllowDelete()){
	    	return false;
	    }
	    return true;
	}
}
