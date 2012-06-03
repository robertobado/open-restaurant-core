package net.openrally.restaurant.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;

public class ProductRequestBody extends BaseRequestBody {

	@ParameterRequired
	private String name;
	
	private String description;
	
	@ParameterRequired
	private Double price;

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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}
