package net.openrally.restaurant.core.exposure.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.exception.ConflictException;
import net.openrally.restaurant.core.exception.ForbiddenException;
import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.persistence.dao.CompanyDAO;
import net.openrally.restaurant.core.persistence.dao.ConfigurationDAO;
import net.openrally.restaurant.core.persistence.dao.PermissionDAO;
import net.openrally.restaurant.core.persistence.dao.RoleDAO;
import net.openrally.restaurant.core.persistence.dao.UserDAO;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.response.body.CompanyResponseBody;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Path("/company")
@Component
@Transactional
public class CompanyResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String PATH = "company";
	
	@Autowired
	private CompanyDAO companyDAO;
	
	@Autowired
	private ConfigurationDAO configurationDAO;
	
	@Autowired
	private RoleDAO roleDAO;
	
	@Autowired
	private PermissionDAO permissionDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response post()
			throws BadRequestException, ForbiddenException, URISyntaxException,
			UnauthorizedException, ConflictException {
		
		Company company = new Company();
		
		Role role = new Role();
		role.setCompany(company);
		role.setName("Administrator");
		role.setDescription("Default administrator role");
		
		Permission permission = new Permission();
		permission.setRole(role);
		permission.setPath(BaseResource.SLASH);
		permission.setAllowPost(true);
		permission.setAllowPut(true);
		permission.setAllowGet(true);
		permission.setAllowDelete(true);
		
		Configuration configuration = new Configuration();
		configuration.setCompany(company);
		configuration.setLoginTokenLifeTime(1800L);
		configuration.setHashSalt(RandomGenerator.generateString(10));
		
		String username = "administrator";
		String password = RandomGenerator.generateString(10);
		
		User user = new User();
		user.setCompany(company);
		user.setLogin(username);
		user.setPasswordHash(User.generatePasswordHash(password, configuration.getHashSalt()));
		
		Set<Role> roleSet = new HashSet<Role>();
		roleSet.add(role);
		user.setRoles(roleSet);
		
		try{
			companyDAO.save(company);
			roleDAO.save(role);
			permissionDAO.save(permission);
			configurationDAO.save(configuration);
			userDAO.save(user);
			userDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}
		
		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ company.getCompanyId());

		logger.debug("Finished processing request successfully");
		
		CompanyResponseBody entityResponseBody = new CompanyResponseBody();
		
		entityResponseBody.setCompanyId(company.getCompanyId());
		entityResponseBody.setUsername(username);
		entityResponseBody.setPassword(password);

		return Response.created(locationURI).entity(gson.toJson(entityResponseBody)).build();
		
	}

}