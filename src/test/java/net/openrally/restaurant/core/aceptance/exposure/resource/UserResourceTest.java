package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.UserResource;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.UserRequestBody;
import net.openrally.restaurant.core.response.body.UserListResponseBody;
import net.openrally.restaurant.core.response.body.UserResponseBody;
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
public class UserResourceTest extends BaseResourceTest{

	private Company company;
	private Configuration configuration;
	private User user;
	private User testUser;
	private LoginToken loginToken;
	private Role role;
	private Permission permission;
	
	public UserResourceTest() throws Exception {
		super();
	}
	
	@Before
	public void setupEntities() {
		company = createCompanyAndPersist();
		configuration = createConfigurationAndPersist(company);
		user = createUserAndPersist(configuration);
		testUser = createUserAndPersist(configuration);
		loginToken = createLoginTokenAndPersist(user);
		authorizedToken = loginToken.getToken();
		role = createRoleAndPersist(company);
		associateRoleWithUser(role, user);
		permission = createFullPermissionAndPersist(role);
	}

	@After
	public void tearDownEntities() {
		
		if(null != testUser){
			userDAO.delete(testUser);
		}
		
		permissionDAO.delete(permission);
		
		user.setRoles(null);
		userDAO.update(user);
		
		roleDAO.delete(role);
		
		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	private void tearDownEntities(String userUri) {
		String roleIdString = userUri.replace(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + UserResource.PATH + BaseResource.SLASH,
				"");

		long userId = Long.parseLong(roleIdString);

		User user = userDAO.get(userId);
		userDAO.delete(user);

	}
	
	@Test
	public void testWrongContentTypePost() throws ClientProtocolException,
			IOException {
		testWrongContentTypePost(UserResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(UserResource.PATH);
	}

	@Test
	public void testInvalidJsonPost() throws ClientProtocolException,
			IOException {
		testInvalidJsonPost(UserResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(UserResource.PATH);
	}

	@Test
	public void testWrongContentTypePut() throws ClientProtocolException,
			IOException {
		testWrongContentTypePut(UserResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePut() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePut(UserResource.PATH);
	}

	@Test
	public void testInvalidJsonPut() throws ClientProtocolException,
			IOException {
		testInvalidJsonPut(UserResource.PATH + BaseResource.SLASH
				+ testUser.getUserId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(UserResource.PATH + BaseResource.SLASH
				+ testUser.getUserId());
	}

	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException,
			IOException {
		testWrongContentTypeGet(UserResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeGet() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeGet(UserResource.PATH);
	}

	@Test
	public void testWrongContentTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongContentTypeDelete(UserResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeDelete(UserResource.PATH);
	}
	
	@Test
	public void testPostDuplicateUser() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(UserResource.PATH);

		UserRequestBody userRequestBody = generateBasicUserRequestBody();

		String requestBody = getGsonInstance().toJson(userRequestBody);

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
	public void testPostMissingLogin() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(UserResource.PATH);

		UserRequestBody userRequestBody = generateBasicUserRequestBody();

		userRequestBody.setLogin(null);

		String requestBody = getGsonInstance().toJson(userRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPostMissingPassword() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(UserResource.PATH);

		UserRequestBody userRequestBody = generateBasicUserRequestBody();

		userRequestBody.setPassword(null);

		String requestBody = getGsonInstance().toJson(userRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostCorrectUser() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(UserResource.PATH);

		UserRequestBody userRequestBody = generateBasicUserRequestBody();

		String requestBody = getGsonInstance().toJson(userRequestBody);

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
	public void testPostUserWithNonExistingRole() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(UserResource.PATH);

		UserRequestBody userRequestBody = generateBasicUserRequestBody();
		
		List<Long> roles = new LinkedList<Long>();
		roles.add(role.getRoleId() + 99);
		userRequestBody.setRoles(roles);

		String requestBody = getGsonInstance().toJson(userRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());

	}
	
	@Test
	public void testPostUserWithOtherCompanysRole() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(UserResource.PATH);

		UserRequestBody userRequestBody = generateBasicUserRequestBody();
		
		Company company = createCompanyAndPersist();
		Role role = createRoleAndPersist(company);
		
		List<Long> roles = new LinkedList<Long>();
		roles.add(role.getRoleId());
		userRequestBody.setRoles(roles);

		String requestBody = getGsonInstance().toJson(userRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		roleDAO.delete(role);
		companyDAO.delete(company);

	}
	
	@Test
	public void testGetInvalidUser() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetUnexistingUser() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectUser() throws ClientProtocolException,
			IOException {
		
		Role role2 = createRoleAndPersist(company);
		Role role3 = createRoleAndPersist(company);
		
		Set<Role> userRoles = new HashSet<Role>();
		userRoles.add(role2);
		userRoles.add(role3);
		testUser.setRoles(userRoles);
		userDAO.update(testUser);
		
		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		UserResponseBody userResponseBody = gson.fromJson(responseBody,
				UserResponseBody.class);

		Assert.assertEquals(userResponseBody.getUserId(), testUser.getUserId());
		Assert.assertEquals(userResponseBody.getLogin(), testUser.getLogin());
		
		Set<Role> roles = testUser.getRoles();
		List<Long> roleIdList = new LinkedList<Long>();
		
		for(Role role : roles){
			roleIdList.add(role.getRoleId());
		}
		
		Assert.assertTrue(roleIdList.containsAll(userResponseBody.getRoles()));
		Assert.assertTrue(userResponseBody.getRoles().containsAll(roleIdList));
		
		testUser.setRoles(null);
		userDAO.update(testUser);
		
		roleDAO.delete(role2);
		roleDAO.delete(role3);
	}

	@Test
	public void testGetOtherCompanysUser() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);

	}
	
	@Test
	public void getFullUserList() throws ClientProtocolException, IOException{
		
		User user2 = createUserAndPersist(configuration);
		User user3 = createUserAndPersist(configuration);
		
		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH );

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		UserListResponseBody objectResponseBody = gson.fromJson(responseBody,
				UserListResponseBody.class);
		
		UserResponseBody userResponseBody1 = new UserResponseBody(testUser);
		UserResponseBody userResponseBody2 = new UserResponseBody(user2);
		UserResponseBody userResponseBody3 = new UserResponseBody(user3);
		
		List<UserResponseBody> list = objectResponseBody.getList();

		Assert.assertTrue(list.contains(userResponseBody1));
		Assert.assertTrue(list.contains(userResponseBody2));
		Assert.assertTrue(list.contains(userResponseBody3));
		
		userDAO.delete(user2);
		userDAO.delete(user3);
	}
	
	@Test
	public void testPutInvalidUser() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(UserResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutUnexistingRole() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(UserResource.PATH
				+ BaseResource.SLASH + (testUser.getUserId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingLogin() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId());

		UserRequestBody userRequestBody = generateBasicUserRequestBody();

		userRequestBody.setLogin(null);

		String requestBody = getGsonInstance().toJson(userRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMissingPassword() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId());

		UserRequestBody userRequestBody = generateBasicUserRequestBody();

		userRequestBody.setPassword(null);

		String requestBody = getGsonInstance().toJson(userRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutCorrectUser() throws ClientProtocolException,
			IOException {
		
		Role role2 = createRoleAndPersist(company);
		Role role3 = createRoleAndPersist(company);
		
		List<Long> roleIdList = new LinkedList<Long>();
		roleIdList.add(role.getRoleId());
		roleIdList.add(role2.getRoleId());
		roleIdList.add(role3.getRoleId());

		// Alter entity

		HttpPut httpPut = generateBasicHttpPut(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId());

		UserRequestBody userRequestBody = generateBasicUserRequestBody();
		
		userRequestBody.setRoles(roleIdList);

		String requestBody = getGsonInstance().toJson(userRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());
		
		// Consume response body to release connection for next request
				StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		UserResponseBody userResponseBody = gson.fromJson(responseBody,
				UserResponseBody.class);

		Assert.assertEquals(userResponseBody.getUserId(), testUser.getUserId());
		Assert.assertEquals(userResponseBody.getLogin(), userRequestBody.getLogin());
		
		Assert.assertTrue(roleIdList.containsAll(userResponseBody.getRoles()));
		Assert.assertTrue(userResponseBody.getRoles().containsAll(roleIdList));
		
		userDAO.delete(testUser);
		roleDAO.delete(role2);
		roleDAO.delete(role3);
		testUser = null;
	}

	@Test
	public void testPutOtherCompanysUser() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpPut httpPut = generateBasicHttpPut(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId());
		
		UserRequestBody userRequestBody = generateBasicUserRequestBody();

		String requestBody = getGsonInstance().toJson(userRequestBody);

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
		HttpDelete httpDelete = generateBasicHttpDelete(UserResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteUnexistingRole() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(UserResource.PATH
				+ BaseResource.SLASH + (testUser.getUserId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectRole() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		testUser = null;
	}

	@Test
	public void testDeleteOtherCompanysRole() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpDelete httpDelete = generateBasicHttpDelete(UserResource.PATH
				+ BaseResource.SLASH + testUser.getUserId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	// Utilitary functions
		private UserRequestBody generateBasicUserRequestBody() {
			UserRequestBody userRequestBody = new UserRequestBody();

			userRequestBody.setLogin("example-login");
			userRequestBody.setPassword(RandomGenerator.generateString(10));

			return userRequestBody;
		}

}
