package net.openrally.restaurant.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;

public class TaxRequestBody extends BaseRequestBody {
	
	@ParameterRequired
	private String name;
	
	private String description;
	
	@ParameterRequired
	private Double amount;
	
	@ParameterRequired
	private Boolean percentage;

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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Boolean getPercentage() {
		return percentage;
	}

	public void setPercentage(Boolean percentage) {
		this.percentage = percentage;
	}

}
