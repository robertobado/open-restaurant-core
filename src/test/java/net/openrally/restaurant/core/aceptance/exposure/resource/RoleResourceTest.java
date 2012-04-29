package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.RoleResource;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.RoleRequestBody;
import net.openrally.restaurant.core.response.body.RoleResponseBody;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.core.util.StringUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class RoleResourceTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;

	public RoleResourceTest() throws Exception {
		super();
	}

	@Before
	public void setupEntities() {
		company = createCompanyAndPersist();
		configuration = createConfigurationAndPersist(company);

		String randomPassword = RandomGenerator.generateString();
		user = createUserAndPersist(configuration, randomPassword);

		authorizedToken = RandomGenerator.generateString(10);

		loginToken = new LoginToken();
		loginToken
				.setExpirationTime((System.currentTimeMillis() / 1000) + 1000);
		loginToken.setToken(authorizedToken);
		loginToken.setUser(user);

		loginTokenDAO.save(loginToken);

		role = new Role();
		role.setCompany(company);
		role.setDescription(RandomGenerator.generateString(50));
		role.setName(RandomGenerator.generateString(10));
		roleDAO.save(role);

	}

	@After
	public void tearDownEntities() {
		
		if(null != role){
			roleDAO.delete(role);
		}
		
		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}

	private void tearDownEntities(String roleUri) {
		String roleIdString = roleUri.replace(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + RoleResource.PATH + BaseResource.SLASH,
				"");

		long roleId = Long.parseLong(roleIdString);

		Role role = roleDAO.get(roleId);
		roleDAO.delete(role);

	}

	@Test
	public void testWrongContentTypePost() throws ClientProtocolException,
			IOException {
		testWrongContentTypePost(RoleResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(RoleResource.PATH);
	}

	@Test
	public void testInvalidJsonPost() throws ClientProtocolException,
			IOException {
		testInvalidJsonPost(RoleResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(RoleResource.PATH);
	}

	@Test
	public void testWrongContentTypePut() throws ClientProtocolException,
			IOException {
		testWrongContentTypePut(RoleResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePut() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePut(RoleResource.PATH);
	}

	@Test
	public void testInvalidJsonPut() throws ClientProtocolException,
			IOException {
		testInvalidJsonPut(RoleResource.PATH + BaseResource.SLASH
				+ role.getRoleId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(RoleResource.PATH + BaseResource.SLASH
				+ role.getRoleId());
	}

	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException,
			IOException {
		testWrongContentTypeGet(RoleResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeGet() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeGet(RoleResource.PATH);
	}

	@Test
	public void testWrongContentTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongContentTypeDelete(RoleResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeDelete(RoleResource.PATH);
	}

	@Test
	public void testPostDuplicateRole() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(RoleResource.PATH);

		RoleRequestBody roleRequestBody = generateBasicRoleRequestBody();

		String requestBody = getGsonInstance().toJson(roleRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());

		tearDownEntities(locationHeader.getValue());
	}

	@Test
	public void testPostMissingRoleName() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(RoleResource.PATH);

		RoleRequestBody roleRequestBody = generateBasicRoleRequestBody();

		roleRequestBody.setName(null);

		String requestBody = getGsonInstance().toJson(roleRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostCorrectRole() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(RoleResource.PATH);

		RoleRequestBody roleRequestBody = generateBasicRoleRequestBody();

		String requestBody = getGsonInstance().toJson(roleRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location));

		tearDownEntities(location);

	}

	@Test
	public void testGetInvalidRole() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(RoleResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetUnexistingRole() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(RoleResource.PATH
				+ BaseResource.SLASH + role.getRoleId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectRole() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(RoleResource.PATH
				+ BaseResource.SLASH + role.getRoleId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		RoleResponseBody roleResponseBody = gson.fromJson(responseBody,
				RoleResponseBody.class);

		Assert.assertEquals(roleResponseBody.getRoleId(), role.getRoleId());
		Assert.assertEquals(roleResponseBody.getName(), role.getName());
		Assert.assertEquals(roleResponseBody.getDescription(),
				role.getDescription());
	}

	@Test
	public void testGetOtherCompanysRole() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);

		String randomPassword = RandomGenerator.generateString();
		User user = createUserAndPersist(configuration, randomPassword);

		authorizedToken = RandomGenerator.generateString(10);

		LoginToken loginToken = new LoginToken();
		loginToken
				.setExpirationTime((System.currentTimeMillis() / 1000) + 1000);
		loginToken.setToken(authorizedToken);
		loginToken.setUser(user);

		loginTokenDAO.save(loginToken);

		HttpGet httpGet = generateBasicHttpGet(RoleResource.PATH
				+ BaseResource.SLASH + role.getRoleId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);

	}

	@Test
	public void testPutInvalidRole() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(RoleResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutUnexistingRole() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(RoleResource.PATH
				+ BaseResource.SLASH + (role.getRoleId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingRoleName() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(RoleResource.PATH
				+ BaseResource.SLASH + role.getRoleId());

		RoleRequestBody roleRequestBody = generateBasicRoleRequestBody();

		roleRequestBody.setName(null);

		String requestBody = getGsonInstance().toJson(roleRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutCorrectRole() throws ClientProtocolException,
			IOException {

		// Alter entity

		HttpPut httpPut = generateBasicHttpPut(RoleResource.PATH
				+ BaseResource.SLASH + role.getRoleId());

		RoleRequestBody roleRequestBody = generateBasicRoleRequestBody();

		String requestBody = getGsonInstance().toJson(roleRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());
		
		// Consume response body to release connection for next request
				StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(RoleResource.PATH
				+ BaseResource.SLASH + role.getRoleId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		RoleResponseBody roleResponseBody = gson.fromJson(responseBody,
				RoleResponseBody.class);

		Assert.assertEquals(roleResponseBody.getRoleId(), role.getRoleId());
		Assert.assertEquals(roleResponseBody.getName(), "example-role-name");
		Assert.assertEquals(roleResponseBody.getDescription(),
				"Example Role Name");
	}

	@Test
	public void testPutOtherCompanysRole() throws ClientProtocolException,
			IOException {
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);

		String randomPassword = RandomGenerator.generateString();
		User user = createUserAndPersist(configuration, randomPassword);

		authorizedToken = RandomGenerator.generateString(10);

		LoginToken loginToken = new LoginToken();
		loginToken
				.setExpirationTime((System.currentTimeMillis() / 1000) + 1000);
		loginToken.setToken(authorizedToken);
		loginToken.setUser(user);

		loginTokenDAO.save(loginToken);

		HttpPut httpPut = generateBasicHttpPut(RoleResource.PATH
				+ BaseResource.SLASH + role.getRoleId());
		
		RoleRequestBody roleRequestBody = generateBasicRoleRequestBody();

		String requestBody = getGsonInstance().toJson(roleRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	@Test
	public void testDeleteIncorrectRole() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(RoleResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteUnexistingRole() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(RoleResource.PATH
				+ BaseResource.SLASH + (role.getRoleId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectRole() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(RoleResource.PATH
				+ BaseResource.SLASH + role.getRoleId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		role = null;
	}

	@Test
	public void testDeleteOtherCompanysRole() throws ClientProtocolException,
			IOException {
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);

		String randomPassword = RandomGenerator.generateString();
		User user = createUserAndPersist(configuration, randomPassword);

		authorizedToken = RandomGenerator.generateString(10);

		LoginToken loginToken = new LoginToken();
		loginToken
				.setExpirationTime((System.currentTimeMillis() / 1000) + 1000);
		loginToken.setToken(authorizedToken);
		loginToken.setUser(user);

		loginTokenDAO.save(loginToken);

		HttpDelete httpDelete = generateBasicHttpDelete(RoleResource.PATH
				+ BaseResource.SLASH + role.getRoleId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}

	// Utilitary functions
	private RoleRequestBody generateBasicRoleRequestBody() {
		RoleRequestBody roleRequestBody = new RoleRequestBody();

		roleRequestBody.setName("example-role-name");
		roleRequestBody.setDescription("Example Role Name");

		return roleRequestBody;
	}

}
