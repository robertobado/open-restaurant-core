package net.openrally.restaurant.core.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;

public class RoleRequestBody extends BaseRequestBody{
	
	@ParameterRequired
	private String name;
	
	@ParameterRequired
	private String description;
	
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
	

}
