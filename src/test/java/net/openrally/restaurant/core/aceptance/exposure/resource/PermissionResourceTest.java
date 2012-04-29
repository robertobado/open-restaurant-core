package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.PermissionResource;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.PermissionRequestBody;
import net.openrally.restaurant.core.response.body.PermissionListResponseBody;
import net.openrally.restaurant.core.response.body.PermissionResponseBody;
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
public class PermissionResourceTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Permission permission;
	
	public PermissionResourceTest() throws Exception {
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
		
		permission = new Permission();
		permission.setRole(role);
		permission.setPath("/xpto");
		permission.setAllowPost(true);
		permission.setAllowPut(false);
		permission.setAllowGet(true);
		permission.setAllowDelete(false);
		
		permissionDAO.save(permission);

	}
	
	@After
	public void tearDownEntities() {
		
		if(null != permission){
			permissionDAO.delete(permission);
		}
		
		roleDAO.delete(role);
		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	private void tearDownEntities(String permissionUri) {
		String permissionIdString = permissionUri.replace(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PermissionResource.PATH + BaseResource.SLASH,
				"");

		long permissionId = Long.parseLong(permissionIdString);

		Permission permission = permissionDAO.get(permissionId);
		permissionDAO.delete(permission);

	}
	
	@Test
	public void testWrongContentTypePost() throws ClientProtocolException,
			IOException {
		testWrongContentTypePost(PermissionResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(PermissionResource.PATH);
	}

	@Test
	public void testInvalidJsonPost() throws ClientProtocolException,
			IOException {
		testInvalidJsonPost(PermissionResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(PermissionResource.PATH);
	}

	@Test
	public void testWrongContentTypePut() throws ClientProtocolException,
			IOException {
		testWrongContentTypePut(PermissionResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePut() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePut(PermissionResource.PATH);
	}

	@Test
	public void testInvalidJsonPut() throws ClientProtocolException,
			IOException {
		testInvalidJsonPut(PermissionResource.PATH + BaseResource.SLASH
				+ permission.getPermissionId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(PermissionResource.PATH + BaseResource.SLASH
				+ permission.getPermissionId());
	}

	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException,
			IOException {
		testWrongContentTypeGet(PermissionResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeGet() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeGet(PermissionResource.PATH);
	}

	@Test
	public void testWrongContentTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongContentTypeDelete(PermissionResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeDelete(PermissionResource.PATH);
	}
	
	@Test
	public void testPostWithPathNotStartedWithSlash() throws ClientProtocolException, IOException{
		HttpPost httpPost = generateBasicHttpPost(PermissionResource.PATH);

		PermissionRequestBody roleRequestBody = generateBasicPermissionRequestBody();

		roleRequestBody.setPath("example-path");

		String requestBody = getGsonInstance().toJson(roleRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostMissingPath() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(PermissionResource.PATH);

		PermissionRequestBody roleRequestBody = generateBasicPermissionRequestBody();

		roleRequestBody.setPath(null);

		String requestBody = getGsonInstance().toJson(roleRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostCorrectPermission() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(PermissionResource.PATH);

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

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
	public void testPostDuplicatePermission() throws ClientProtocolException,
			IOException {
		
		HttpPost httpPost = generateBasicHttpPost(PermissionResource.PATH);

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location));
		
		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());

		tearDownEntities(locationHeader.getValue());
	}
	
	@Test
	public void testPostPermissionOnOtherCompanysRole() throws ClientProtocolException, IOException{
		Company company = createCompanyAndPersist();

		Role role = new Role();
		role.setCompany(company);
		role.setDescription(RandomGenerator.generateString(50));
		role.setName(RandomGenerator.generateString(10));
		roleDAO.save(role);
		
		HttpPost httpPost = generateBasicHttpPost(PermissionResource.PATH);

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();
		
		permissionRequestBody.setRoleId(role.getRoleId());

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		roleDAO.delete(role);
		companyDAO.delete(company);

	}
	

	@Test
	public void testGetInvalidRole() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetUnexistingRole() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectPermission() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		PermissionResponseBody permissionResponseBody = gson.fromJson(responseBody,
				PermissionResponseBody.class);

		Assert.assertEquals(permissionResponseBody.getPermissionId(), permission.getPermissionId());
		Assert.assertEquals(permissionResponseBody.getRoleId(), permission.getRole().getRoleId());
		Assert.assertTrue(StringUtils.equals(permissionResponseBody.getPath(), permission.getPath()));
		Assert.assertEquals(permissionResponseBody.isAllowPost(), permission.isAllowPost());
		Assert.assertEquals(permissionResponseBody.isAllowPut(), permission.isAllowPut());
		Assert.assertEquals(permissionResponseBody.isAllowGet(), permission.isAllowGet());
		Assert.assertEquals(permissionResponseBody.isAllowDelete(), permission.isAllowDelete());
	}

	@Test
	public void testGetOtherCompanysPermission() throws ClientProtocolException,
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

		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);

	}
	
	@Test
	public void testGetListWithInvalidRoleId() throws ClientProtocolException, IOException{
		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ "?roleId=" + "xpto");
		
		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testGetListWithNonExistingRoleId() throws ClientProtocolException, IOException{
		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ "?roleId=" + role.getRoleId() + 99);
		
		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testGetListCorrectly() throws ClientProtocolException, IOException{
		
		Permission permission2 = new Permission();
		permission2.setAllowPost(true);
		permission2.setAllowPut(true);
		permission2.setAllowGet(true);
		permission2.setAllowDelete(true);
		permission2.setRole(role);
		permission2.setPath(permission.getPath() + "-2");
		permissionDAO.save(permission2);
		
		Permission permission3 = new Permission();
		permission3.setAllowPost(false);
		permission3.setAllowPut(false);
		permission3.setAllowGet(false);
		permission3.setAllowDelete(false);
		permission3.setRole(role);
		permission3.setPath(permission.getPath() + "-3");
		permissionDAO.save(permission3);
		
		
		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ "?roleId=" + role.getRoleId());
		
		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		PermissionListResponseBody permissionResponseBody = gson.fromJson(responseBody,
				PermissionListResponseBody.class);
		
		PermissionResponseBody permissionResponseBody1 = new PermissionResponseBody(permission);
		PermissionResponseBody permissionResponseBody2 = new PermissionResponseBody(permission2);
		PermissionResponseBody permissionResponseBody3 = new PermissionResponseBody(permission3);
		
		List<PermissionResponseBody> permissionsList = permissionResponseBody.getList();
		
		Assert.assertTrue(permissionsList.contains(permissionResponseBody1));
		Assert.assertTrue(permissionsList.contains(permissionResponseBody2));
		Assert.assertTrue(permissionsList.contains(permissionResponseBody3));
		
		permissionDAO.delete(permission3);
		permissionDAO.delete(permission2);
	}
	
	@Test
	public void testGetListWithOtherCompanysRole() throws ClientProtocolException,
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

		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ "?roleId=" + role.getRoleId());

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
		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutUnexistingRole() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + (permission.getPermissionId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingPath() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId());

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		permissionRequestBody.setPath(null);

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	
	@Test
	public void testPuttWithPathNotStartedWithSlash() throws ClientProtocolException, IOException{
		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId());

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		permissionRequestBody.setPath("example-path");

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutCorrectRole() throws ClientProtocolException,
			IOException {

		// Alter entity

		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId());

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());
		
		// Consume response body to release connection for next request
				StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		PermissionResponseBody permissionResponseBody = gson.fromJson(responseBody,
				PermissionResponseBody.class);

		Assert.assertEquals(permissionResponseBody.getPermissionId(), permission.getPermissionId());
		Assert.assertTrue(StringUtils.equals(permissionResponseBody.getPath(), permissionRequestBody.getPath()));
		Assert.assertEquals(permissionResponseBody.getRoleId(), permissionRequestBody.getRoleId());
		Assert.assertEquals(permissionResponseBody.isAllowPost(), permissionRequestBody.isAllowPost());
		Assert.assertEquals(permissionResponseBody.isAllowPut(), permissionRequestBody.isAllowPut());
		Assert.assertEquals(permissionResponseBody.isAllowGet(), permissionRequestBody.isAllowGet());
		Assert.assertEquals(permissionResponseBody.isAllowDelete(), permissionRequestBody.isAllowDelete());
	}

	@Test
	public void testPutOtherCompanysPermission() throws ClientProtocolException,
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

		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId());
		
		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

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
		HttpDelete httpDelete = generateBasicHttpDelete(PermissionResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteUnexistingRole() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(PermissionResource.PATH
				+ BaseResource.SLASH + (permission.getPermissionId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectRole() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		permission = null;
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

		HttpDelete httpDelete = generateBasicHttpDelete(PermissionResource.PATH
				+ BaseResource.SLASH + permission.getPermissionId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	// Utilitary functions
	private PermissionRequestBody generateBasicPermissionRequestBody() {
		PermissionRequestBody permissionRequestBody = new PermissionRequestBody();

		permissionRequestBody.setRoleId(role.getRoleId());
		permissionRequestBody.setPath("/example-path");
		permissionRequestBody.setAllowPost(false);
		permissionRequestBody.setAllowPut(true);
		permissionRequestBody.setAllowGet(false);
		permissionRequestBody.setAllowDelete(true);

		return permissionRequestBody;
	}

}
