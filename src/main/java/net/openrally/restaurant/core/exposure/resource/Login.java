package net.openrally.restaurant.core.exposure.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.openrally.restaurant.core.persistence.dao.ConfigurationDAO;
import net.openrally.restaurant.core.persistence.dao.UserDAO;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.LoginRequestBody;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.spi.resource.Singleton;

@Path("/login")
@Component
@Transactional
@Singleton
@Produces("application/json")
@Consumes("application/json")
public class Login extends BaseResource{
	
	@Autowired
	public ConfigurationDAO configurationDAO;
	
	@Autowired
	public UserDAO userDAO;
		
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}

	@POST
	public Response post(String requestBody){
		
		LoginRequestBody loginRequestBody = gson.fromJson(requestBody, LoginRequestBody.class);
		
		if(null == loginRequestBody || 
				StringUtils.isEmpty(loginRequestBody.getLogin()) ||
				StringUtils.isEmpty(loginRequestBody.getCompanyId()) ||
				StringUtils.isEmpty(loginRequestBody.getPassword())){
		return Response.status(Status.BAD_REQUEST).build();
			
		}
		
		String companyId = loginRequestBody.getCompanyId();
		String login = loginRequestBody.getLogin();
		
		User user = userDAO.loadByCompanyIdAndLogin(companyId, login);
		
		if(null == user){
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		Configuration configuration = configurationDAO.loadByCompanyId(companyId);
		
		return Response.status(Status.GONE).build();
	}
	
}
