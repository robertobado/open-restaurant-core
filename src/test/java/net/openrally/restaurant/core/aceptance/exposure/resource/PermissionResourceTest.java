package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.lang.reflect.Type;
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
import net.openrally.restaurant.core.util.StringUtilities;
import net.openrally.restaurant.request.body.PermissionRequestBody;
import net.openrally.restaurant.response.body.PermissionResponseBody;

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
public class PermissionResourceTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Permission testPermission;
	private Permission permission;
	
	public PermissionResourceTest() throws Exception {
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
		testPermission = createRandomPermissionAndPersist(role);
		permission = createFullPermissionAndPersist(role);
	}
	
	@After
	public void tearDownEntities() {
		
		if(null != testPermission){
			permissionDAO.delete(testPermission);
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
				+ testPermission.getPermissionId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(PermissionResource.PATH + BaseResource.SLASH
				+ testPermission.getPermissionId());
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

		PermissionRequestBody entityRequestBody = generateBasicPermissionRequestBody();

		entityRequestBody.setPath("example-path");

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostMissingPath() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(PermissionResource.PATH);

		PermissionRequestBody entityRequestBody = generateBasicPermissionRequestBody();

		entityRequestBody.setPath(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

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

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location));

		deleteEntityBasedOnLocation(location, permissionDAO);

	}
	
	@Test
	public void testPostDuplicatePermission() throws ClientProtocolException,
			IOException {
		
		HttpPost httpPost = generateBasicHttpPost(PermissionResource.PATH);

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

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

		deleteEntityBasedOnLocation(location, permissionDAO);
	}
	
	@Test
	public void testPostPermissionOnOtherCompanysRole() throws ClientProtocolException, IOException{
		Company company = createCompanyAndPersist();

		Role role = createRoleAndPersist(company);
		
		HttpPost httpPost = generateBasicHttpPost(PermissionResource.PATH);

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();
		
		permissionRequestBody.setRoleId(role.getRoleId());

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		roleDAO.delete(role);
		companyDAO.delete(company);

	}
	

	@Test
	public void testGetInvalidPermission() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetUnexistingPermission() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectPermission() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		PermissionResponseBody permissionResponseBody = gson.fromJson(responseBody,
				PermissionResponseBody.class);

		Assert.assertEquals(permissionResponseBody.getPermissionId(), testPermission.getPermissionId());
		Assert.assertEquals(permissionResponseBody.getRoleId(), testPermission.getRole().getRoleId());
		Assert.assertTrue(StringUtils.equals(permissionResponseBody.getPath(), testPermission.getPath()));
		Assert.assertEquals(permissionResponseBody.isAllowPost(), testPermission.isAllowPost());
		Assert.assertEquals(permissionResponseBody.isAllowPut(), testPermission.isAllowPut());
		Assert.assertEquals(permissionResponseBody.isAllowGet(), testPermission.isAllowGet());
		Assert.assertEquals(permissionResponseBody.isAllowDelete(), testPermission.isAllowDelete());
	}

	@Test
	public void testGetOtherCompanysPermission() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId());

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
		
		Permission permission2 = createRandomPermissionAndPersist(role);
		
		Permission permission3 = createRandomPermissionAndPersist(role);		
		
		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ "?roleId=" + role.getRoleId());
		
		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		Type listType = new TypeToken<List<PermissionResponseBody>>() {}.getType();
		
		List<PermissionResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);
		
		PermissionResponseBody permissionResponseBody1 = new PermissionResponseBody(testPermission);
		PermissionResponseBody permissionResponseBody2 = new PermissionResponseBody(permission2);
		PermissionResponseBody permissionResponseBody3 = new PermissionResponseBody(permission3);
		
		Assert.assertTrue(entityResponseBodyList.contains(permissionResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(permissionResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(permissionResponseBody3));
		
		permissionDAO.delete(permission3);
		permissionDAO.delete(permission2);
	}
	
	@Test
	public void testGetListWithOtherCompanysRole() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

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
	public void testPutInvalidPermission() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutUnexistingPermission() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + (testPermission.getPermissionId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingPath() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId());

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		permissionRequestBody.setPath(null);

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	
	@Test
	public void testPuttWithPathNotStartedWithSlash() throws ClientProtocolException, IOException{
		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId());

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		permissionRequestBody.setPath("example-path");

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutCorrectPermission() throws ClientProtocolException,
			IOException {

		// Alter entity

		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId());

		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());
		
		// Consume response body to release connection for next request
				StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		PermissionResponseBody permissionResponseBody = gson.fromJson(responseBody,
				PermissionResponseBody.class);

		Assert.assertEquals(permissionResponseBody.getPermissionId(), testPermission.getPermissionId());
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
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpPut httpPut = generateBasicHttpPut(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId());
		
		PermissionRequestBody permissionRequestBody = generateBasicPermissionRequestBody();

		String requestBody = getGsonInstance().toJson(permissionRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	@Test
	public void testDeleteIncorrectPermission() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(PermissionResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteUnexistingPermission() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(PermissionResource.PATH
				+ BaseResource.SLASH + (testPermission.getPermissionId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectPermission() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		testPermission = null;
	}

	@Test
	public void testDeleteOtherCompanysPermission() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpDelete httpDelete = generateBasicHttpDelete(PermissionResource.PATH
				+ BaseResource.SLASH + testPermission.getPermissionId());

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
