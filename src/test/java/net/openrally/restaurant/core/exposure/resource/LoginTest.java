package net.openrally.restaurant.core.exposure.resource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.persistence.dao.CompanyDAO;
import net.openrally.restaurant.core.persistence.dao.ConfigurationDAO;
import net.openrally.restaurant.core.persistence.dao.UserDAO;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.LoginRequestBody;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.core.utils.HashCalculator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class LoginTest extends BaseResourceTest {

	private WebResource webResource;

	@Autowired
	private UserDAO userDAO;
	private User user;

	@Autowired
	private CompanyDAO companyDAO;
	private Company company;

	@Autowired
	private ConfigurationDAO configurationDAO;
	private Configuration configuration;
	
	private String randomPassword;

	public LoginTest() throws Exception {
		super("net.openrally.restaurant.core.exposure.resource");
	}

	@Before
	public void before() {
		this.webResource = resource();

	}
	
	public void setupEntities(){
		createCompanyAndPersist();
		createConfigurationAndPersist();
		createUserAndPersist();
	}

	@Test
	public void testWrongContentType() {
		ClientResponse response = webResource.path("/login")
				.header(CONTENT_TYPE, MediaType.APPLICATION_XML)
				.post(ClientResponse.class);
		Assert.assertEquals(Status.UNSUPPORTED_MEDIA_TYPE.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(),
				response.getStatus());
	}

	@Test
	public void testWrongAcceptType() {
		ClientResponse response = webResource.path("/login")
				.accept(MediaType.APPLICATION_XML).post(ClientResponse.class);
		Assert.assertEquals(Status.NOT_ACCEPTABLE.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(),
				response.getStatus());
	}

	@Test
	public void testEmptyRequestBody() {
		ClientResponse response = webResource.path("/login")
				.accept(MediaType.APPLICATION_JSON)
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(ClientResponse.class);
		Assert.assertEquals(Status.BAD_REQUEST.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(),
				response.getStatus());
	}

	@Test
	public void testEmptyLogin() {
		LoginRequestBody loginRequestBody = new LoginRequestBody();
		loginRequestBody.setCompanyId(RandomGenerator.generateString());
		loginRequestBody.setPassword(RandomGenerator.generateString());
		String requestBody = gson.toJson(loginRequestBody);

		ClientResponse response = webResource.path("/login")
				.accept(MediaType.APPLICATION_JSON)
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, requestBody);
		Assert.assertEquals(Status.BAD_REQUEST.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(),
				response.getStatus());
	}

	@Test
	public void testEmptyCompanyId() {
		LoginRequestBody loginRequestBody = new LoginRequestBody();
		loginRequestBody.setLogin(RandomGenerator.generateString());
		loginRequestBody.setPassword(RandomGenerator.generateString());
		String requestBody = gson.toJson(loginRequestBody);

		ClientResponse response = webResource.path("/login")
				.accept(MediaType.APPLICATION_JSON)
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, requestBody);
		Assert.assertEquals(Status.BAD_REQUEST.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(),
				response.getStatus());
	}

	@Test
	public void testEmptyPassword() {
		LoginRequestBody loginRequestBody = new LoginRequestBody();
		loginRequestBody.setLogin(RandomGenerator.generateString());
		loginRequestBody.setCompanyId(RandomGenerator.generateString());
		String requestBody = gson.toJson(loginRequestBody);

		ClientResponse response = webResource.path("/login")
				.accept(MediaType.APPLICATION_JSON)
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, requestBody);
		Assert.assertEquals(Status.BAD_REQUEST.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(),
				response.getStatus());
	}
	
	@Test
	public void testInvalidCompany() {
		setupEntities();
		
		LoginRequestBody loginRequestBody = new LoginRequestBody();
		loginRequestBody.setLogin(user.getLogin());
		loginRequestBody.setCompanyId("Invalid Company");
		loginRequestBody.setPassword(randomPassword);
		String requestBody = gson.toJson(loginRequestBody);

		ClientResponse response = webResource.path("/login")
				.accept(MediaType.APPLICATION_JSON)
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, requestBody);
		Assert.assertEquals(Status.UNAUTHORIZED.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				response.getStatus());

	}
	
	@Test
	public void testInvalidLogin() {
		setupEntities();
		
		LoginRequestBody loginRequestBody = new LoginRequestBody();
		loginRequestBody.setLogin("Invalid Login");
		loginRequestBody.setCompanyId(user.getCompany().getCompanyId());
		loginRequestBody.setPassword(randomPassword);
		String requestBody = gson.toJson(loginRequestBody);

		ClientResponse response = webResource.path("/login")
				.accept(MediaType.APPLICATION_JSON)
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, requestBody);
		Assert.assertEquals(Status.UNAUTHORIZED.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				response.getStatus());

	}
	
	@Test
	public void testInvalidPassword() {
		setupEntities();
		
		LoginRequestBody loginRequestBody = new LoginRequestBody();
		loginRequestBody.setLogin(user.getLogin());
		loginRequestBody.setCompanyId(user.getCompany().getCompanyId());
		loginRequestBody.setPassword("Invalid Password");
		String requestBody = gson.toJson(loginRequestBody);

		ClientResponse response = webResource.path("/login")
				.accept(MediaType.APPLICATION_JSON)
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, requestBody);
		Assert.assertEquals(Status.UNAUTHORIZED.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				response.getStatus());

	}
	
	@Test
	public void testCorrectCredentials() {
		setupEntities();
		
		LoginRequestBody loginRequestBody = new LoginRequestBody();
		loginRequestBody.setLogin(user.getLogin());
		loginRequestBody.setCompanyId(user.getCompany().getCompanyId());
		loginRequestBody.setPassword(randomPassword);
		String requestBody = gson.toJson(loginRequestBody);

		ClientResponse response = webResource.path("/login")
				.accept(MediaType.APPLICATION_JSON)
				.header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, requestBody);
		Assert.assertEquals(Status.UNAUTHORIZED.toString(), response
				.getClientResponseStatus().toString());
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				response.getStatus());

	}
	
	private void createCompanyAndPersist(){
		company = new Company();
		companyDAO.save(company);
	}
	
	private void createConfigurationAndPersist(){
		configuration = new Configuration();
		configuration.setCompany(company);
		configuration.setHashSalt(RandomGenerator.generateString());
		configurationDAO.save(configuration);
	}

	private void createUserAndPersist() {
		user = new User();
		user.setCompany(company);
		user.setLogin(RandomGenerator.generateString());
		
		randomPassword = RandomGenerator.generateString();
		
		String passwordWithSalt = configuration.getHashSalt() + "/" + randomPassword;
		String passwordHash = HashCalculator.generateMD5Hash(passwordWithSalt);
		
		user.setPasswordHash(passwordHash);
		userDAO.save(user);
	}
}
