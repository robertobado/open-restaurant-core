package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.RoleResource;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.StringUtilities;
import net.openrally.restaurant.request.body.RoleRequestBody;
import net.openrally.restaurant.response.body.RoleResponseBody;

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

import com.google.gson.reflect.TypeToken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class RoleResourceTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Role testRole;
	private Permission permission;

	public RoleResourceTest() throws Exception {
		super();
	}

	@Before
	public void setupEntities() {
		company = createCompanyAndPersist();
		configuration = createConfigurationAndPersist(company);
		user = createUserAndPersist(configuration);
		loginToken = createLoginTokenAndPersist(user);
		authorizedToken = loginToken.getToken();
		role = createRoleAndPersist(company);
		associateRoleWithUser(role, user);
		permission = createFullPermissionAndPersist(role);
		
		testRole = createRoleAndPersist(company);
	}

	@After
	public void tearDownEntities() {
		
		permissionDAO.delete(permission);
		
		user.setRoles(null);
		userDAO.update(user);
		
		roleDAO.delete(role);
		
		if(null != testRole){
			roleDAO.delete(testRole);
		}
		
		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
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
				+ testRole.getRoleId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(RoleResource.PATH + BaseResource.SLASH
				+ testRole.getRoleId());
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

		deleteEntityBasedOnLocation(locationHeader.getValue(), roleDAO);
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

		deleteEntityBasedOnLocation(locationHeader.getValue(), roleDAO);

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
				+ BaseResource.SLASH + testRole.getRoleId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectRole() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(RoleResource.PATH
				+ BaseResource.SLASH + testRole.getRoleId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		RoleResponseBody roleResponseBody = gson.fromJson(responseBody,
				RoleResponseBody.class);

		Assert.assertEquals(roleResponseBody.getRoleId(), testRole.getRoleId());
		Assert.assertEquals(roleResponseBody.getName(), testRole.getName());
		Assert.assertEquals(roleResponseBody.getDescription(),
				testRole.getDescription());
	}

	@Test
	public void testGetOtherCompanysRole() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpGet httpGet = generateBasicHttpGet(RoleResource.PATH
				+ BaseResource.SLASH + testRole.getRoleId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);

	}
	
	@Test
	public void getFullRoleList() throws ClientProtocolException, IOException{
		
		Role role2 = createRoleAndPersist(company);
		Role role3 = createRoleAndPersist(company);
		
		HttpGet httpGet = generateBasicHttpGet(RoleResource.PATH
				+ BaseResource.SLASH );

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);
		
		Type listType = new TypeToken<List<RoleResponseBody>>() {}.getType();
		
		List<RoleResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);
		
		RoleResponseBody roleResponseBody1 = new RoleResponseBody(testRole);
		RoleResponseBody roleResponseBody2 = new RoleResponseBody(role2);
		RoleResponseBody roleResponseBody3 = new RoleResponseBody(role3);

		Assert.assertTrue(entityResponseBodyList.contains(roleResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(roleResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(roleResponseBody3));
		
		roleDAO.delete(role2);
		roleDAO.delete(role3);
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
				+ BaseResource.SLASH + (testRole.getRoleId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingRoleName() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(RoleResource.PATH
				+ BaseResource.SLASH + testRole.getRoleId());

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
				+ BaseResource.SLASH + testRole.getRoleId());

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
				+ BaseResource.SLASH + testRole.getRoleId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		RoleResponseBody roleResponseBody = gson.fromJson(responseBody,
				RoleResponseBody.class);

		Assert.assertEquals(roleResponseBody.getRoleId(), testRole.getRoleId());
		Assert.assertEquals(roleResponseBody.getName(), "example-role-name");
		Assert.assertEquals(roleResponseBody.getDescription(),
				"Example Role Name");
	}

	@Test
	public void testPutOtherCompanysRole() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpPut httpPut = generateBasicHttpPut(RoleResource.PATH
				+ BaseResource.SLASH + testRole.getRoleId());
		
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
				+ BaseResource.SLASH + (testRole.getRoleId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectRole() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(RoleResource.PATH
				+ BaseResource.SLASH + testRole.getRoleId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		testRole = null;
	}

	@Test
	public void testDeleteOtherCompanysRole() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpDelete httpDelete = generateBasicHttpDelete(RoleResource.PATH
				+ BaseResource.SLASH + testRole.getRoleId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	@Test
	public void testDeleteRoleUsedByFK() throws ClientProtocolException, IOException{
		
		Permission permission = createRandomPermissionAndPersist(testRole);
		
		HttpDelete httpDelete = generateBasicHttpDelete(RoleResource.PATH
				+ BaseResource.SLASH + testRole.getRoleId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.CONFLICT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		permissionDAO.delete(permission);
	}

	// Utilitary functions
	private RoleRequestBody generateBasicRoleRequestBody() {
		RoleRequestBody roleRequestBody = new RoleRequestBody();

		roleRequestBody.setName("example-role-name");
		roleRequestBody.setDescription("Example Role Name");

		return roleRequestBody;
	}

}
