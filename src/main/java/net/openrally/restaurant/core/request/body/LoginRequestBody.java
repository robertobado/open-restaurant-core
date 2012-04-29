package net.openrally.restaurant.core.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;
import net.openrally.restaurant.core.annotation.ParameterValuePositive;

public class LoginRequestBody extends BaseRequestBody{
	
	@ParameterRequired
	private String login;
	
	@ParameterRequired
	private String password;
	
	@ParameterRequired
	@ParameterValuePositive
	private long companyId;
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

}
