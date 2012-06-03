package net.openrally.restaurant.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;


public class ConsumptionIdentifierRequestBody extends BaseRequestBody {
	
	@ParameterRequired
	private String identifier;
	
	private String description;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
