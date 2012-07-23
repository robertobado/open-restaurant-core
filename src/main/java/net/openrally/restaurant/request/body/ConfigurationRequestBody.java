package net.openrally.restaurant.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;
import net.openrally.restaurant.core.annotation.ParameterValuePositive;

public class ConfigurationRequestBody extends BaseRequestBody {

	@ParameterRequired
	@ParameterValuePositive
	private Long loginTokenLifeTime;
	
	@ParameterRequired
	private String companyName;
	
	@ParameterRequired
	private String billTemplate;

	public Long getLoginTokenLifeTime() {
		return loginTokenLifeTime;
	}

	public void setLoginTokenLifeTime(Long loginTokenLifeTime) {
		this.loginTokenLifeTime = loginTokenLifeTime;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBillTemplate() {
		return billTemplate;
	}

	public void setBillTemplate(String billTemplate) {
		this.billTemplate = billTemplate;
	}
	
}
