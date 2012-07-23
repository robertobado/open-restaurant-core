package net.openrally.restaurant.response.body;

import net.openrally.restaurant.core.persistence.entity.Configuration;

public class ConfigurationResponseBody {
	
	private Long loginTokenLifeTime;
	
	private String companyName;
	
	private String billTemplate;

	public ConfigurationResponseBody(Configuration entity) {
		this.loginTokenLifeTime = entity.getLoginTokenLifeTime();
		this.companyName = entity.getCompanyName();
		this.billTemplate = entity.getBillTemplate();
	}

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
