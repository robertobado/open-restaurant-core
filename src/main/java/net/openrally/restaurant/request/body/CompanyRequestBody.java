package net.openrally.restaurant.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;

public class CompanyRequestBody extends BaseRequestBody {

	@ParameterRequired
	private String companyName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}
