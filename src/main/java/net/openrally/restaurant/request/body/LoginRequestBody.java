package net.openrally.restaurant.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;

public class LoginRequestBody extends BaseRequestBody{
	
	@ParameterRequired
	private String login;
	
	@ParameterRequired
	private String password;
	
	@ParameterRequired
	private String companyName;
	
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
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
