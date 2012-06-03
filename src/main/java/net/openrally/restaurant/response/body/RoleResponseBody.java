package net.openrally.restaurant.response.body;

import org.apache.commons.lang.StringUtils;

import net.openrally.restaurant.core.persistence.entity.Role;

public class RoleResponseBody {
	
	private Long roleId;
	private String name;
	private String description;
	
	public RoleResponseBody(){
		
	}
	
	public RoleResponseBody(Role role){
		this.roleId = role.getRoleId();
		this.name = role.getName();
		this.description = role.getDescription();
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof RoleResponseBody)) return false;
	    RoleResponseBody otherRoleResponseBody = (RoleResponseBody)other;
	    if(null == otherRoleResponseBody.getRoleId() || !otherRoleResponseBody.getRoleId().equals(this.getRoleId())){
	    	return false;
	    }
	    if(!StringUtils.equals(otherRoleResponseBody.getName(), this.getName())){
	    	return false;
	    }
	    if(!StringUtils.equals(otherRoleResponseBody.getDescription(), this.getDescription())){
	    	return false;
	    }
	    return true;
	}
}
