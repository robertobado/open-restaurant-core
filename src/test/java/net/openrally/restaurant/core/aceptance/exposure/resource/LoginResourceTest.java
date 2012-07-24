package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.LoginResource;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.core.util.StringUtilities;
import net.openrally.restaurant.request.body.LoginRequestBody;
import net.openrally.restaurant.response.body.LoginResponseBody;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class LoginResourceTest extends BaseResourceTest {

	private Company company;

	private Configuration configuration;
	
	private User user;

	private String randomPassword;

	public LoginResourceTest() throws Exception {
		super();
	}

	public void setupEntities() {
		company = createCompanyAndPersist();
		configuration = createConfigurationAndPersist(company);
		
		randomPassword = RandomGenerator.generateString();
		user = createUserAndPersist(configuration, randomPassword);
	}
	
	public void tearDownEntities(String token){
		
		if(!StringUtils.isBlank(token)){
			LoginToken loginToken = loginTokenDAO.loadByToken(token);
			loginTokenDAO.delete(loginToken);
		}
		
		tearDownEntities();
	}
	
	public void tearDownEntities(){	
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}

	@Test
	public void testWrongContentTypePost() throws ClientProtocolException,
			IOException {
		testWrongContentTypePost(LoginResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(LoginResource.PATH);
	}
	
	@Test
	public void testInvalidJsonPost() throws ClientProtocolException, IOException {
		testInvalidJsonPost(LoginResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(LoginResource.PATH);
	}

	@Test
	public void testEmptyLogin() throws ClientProtocolException, IOException {
		setupEntities();
		
		HttpPost httpPost = generateBasicHttpPost(LoginResource.PATH);

		LoginRequestBody loginRequestBody = generateBasicLoginRequestBody();
		loginRequestBody.setLogin(null);
		String requestBody = getGsonInstance().toJson(loginRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		tearDownEntities();
	}

	@Test
	public void testEmptyCompanyId() throws ClientProtocolException,
			IOException {
		setupEntities();
		
		HttpPost httpPost = generateBasicHttpPost(LoginResource.PATH);

		LoginRequestBody loginRequestBody = generateBasicLoginRequestBody();
		loginRequestBody.setCompanyId(null);
		String requestBody = getGsonInstance().toJson(loginRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		tearDownEntities();
	}

	@Test
	public void testEmptyPassword() throws ClientProtocolException, IOException {
		setupEntities();
		
		HttpPost httpPost = generateBasicHttpPost(LoginResource.PATH);

		LoginRequestBody loginRequestBody = generateBasicLoginRequestBody();
		loginRequestBody.setPassword(null);
		String requestBody = getGsonInstance().toJson(loginRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		tearDownEntities();
	}

	@Test
	public void testInvalidCompany() throws ClientProtocolException,
			IOException {
		setupEntities();

		HttpPost httpPost = generateBasicHttpPost(LoginResource.PATH);

		LoginRequestBody loginRequestBody = generateBasicLoginRequestBody();
		loginRequestBody.setCompanyId(-1L);
		String requestBody = getGsonInstance().toJson(loginRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		tearDownEntities();
	}
	
	@Test
	public void testNonExistingCompany() throws ClientProtocolException,
			IOException {
		setupEntities();

		HttpPost httpPost = generateBasicHttpPost(LoginResource.PATH);

		LoginRequestBody loginRequestBody = generateBasicLoginRequestBody();
		loginRequestBody.setCompanyId(company.getCompanyId() + 10);
		String requestBody = getGsonInstance().toJson(loginRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		tearDownEntities();
	}

	@Test
	public void testInvalidLogin() throws ClientProtocolException, IOException {
		setupEntities();

		HttpPost httpPost = this.generateBasicHttpPost(LoginResource.PATH);

		LoginRequestBody loginRequestBody = generateBasicLoginRequestBody();
		loginRequestBody.setLogin("Invalid Login");
		String requestBody = getGsonInstance().toJson(loginRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		tearDownEntities();
	}

	@Test
	public void testInvalidPassword() throws ClientProtocolException,
			IOException {
		setupEntities();

		HttpPost httpPost = generateBasicHttpPost(LoginResource.PATH);

		LoginRequestBody loginRequestBody = generateBasicLoginRequestBody();
		loginRequestBody.setPassword("Invalid Password");
		String requestBody = getGsonInstance().toJson(loginRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		tearDownEntities();
	}

	@Test
	public void testCorrectCredentials() throws ClientProtocolException,
			IOException {
		setupEntities();

		HttpPost httpPost = generateBasicHttpPost(LoginResource.PATH);

		LoginRequestBody loginRequestBody = generateBasicLoginRequestBody();
		String requestBody = getGsonInstance().toJson(loginRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);
		
		LoginResponseBody loginResponseBody = gson.fromJson(responseBody,
				LoginResponseBody.class);

		String token = loginResponseBody.getToken();
		
		Assert.assertFalse(StringUtils.isBlank(loginResponseBody.getToken()));
		
		tearDownEntities(token);
	}
	
	private LoginRequestBody generateBasicLoginRequestBody(){
		
		LoginRequestBody loginRequestBody = new LoginRequestBody();
		loginRequestBody.setLogin(user.getLogin());
		loginRequestBody.setCompanyId(user.getCompany().getCompanyId());
		loginRequestBody.setPassword(randomPassword);
		
		return loginRequestBody;
	}
}
