package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.ConfigurationResource;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.core.util.StringUtilities;
import net.openrally.restaurant.request.body.ConfigurationRequestBody;
import net.openrally.restaurant.response.body.ConfigurationResponseBody;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.spi.container.ContainerRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class ConfigurationResourceTest extends BaseResourceTest {

	public static final int METHOD_NOT_ALLOWED = 405;

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Permission permission;

	public ConfigurationResourceTest() throws Exception {
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
	}

	@After
	public void tearDownEntities() {

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

		HttpPost httpPost = new HttpPost(getServerBasePath()
				+ BaseResource.SLASH + ConfigurationResource.PATH);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_XML);
		httpPost.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpPost.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(METHOD_NOT_ALLOWED, response.getStatusLine()
				.getStatusCode());
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = new HttpPost(getServerBasePath()
				+ BaseResource.SLASH + ConfigurationResource.PATH);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_XML);
		httpPost.addHeader(acceptHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpPost.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(METHOD_NOT_ALLOWED, response.getStatusLine()
				.getStatusCode());
	}

	@Test
	public void testInvalidJsonPost() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(ConfigurationResource.PATH);

		httpPost.setEntity(new StringEntity("Invalid Json"));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(METHOD_NOT_ALLOWED, response.getStatusLine()
				.getStatusCode());
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(ConfigurationResource.PATH);

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(METHOD_NOT_ALLOWED, response.getStatusLine()
				.getStatusCode());
	}

	@Test
	public void testWrongContentTypePut() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = new HttpPut(getServerBasePath() + BaseResource.SLASH
				+ ConfigurationResource.PATH);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_XML);
		httpPut.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpPut.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(),
				response.getStatusLine().getStatusCode());
	}

	@Test
	public void testWrongAcceptTypePut() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = new HttpPut(getServerBasePath() + BaseResource.SLASH
				+ ConfigurationResource.PATH);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_XML);
		httpPut.addHeader(acceptHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpPut.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testInvalidJsonPut() throws ClientProtocolException,
			IOException {
		testInvalidJsonPut(ConfigurationResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(ConfigurationResource.PATH);
	}

	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = new HttpGet(getServerBasePath()
				+ BaseResource.SLASH + ConfigurationResource.PATH);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_XML);
		httpGet.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpGet.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(),
				response.getStatusLine().getStatusCode());
	}

	@Test
	public void testWrongAcceptTypeGet() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = new HttpGet(getServerBasePath()
				+ BaseResource.SLASH + ConfigurationResource.PATH);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_XML);
		httpGet.addHeader(acceptHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpGet.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testWrongContentTypeDelete() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = new HttpDelete(getServerBasePath()
				+ BaseResource.SLASH + ConfigurationResource.PATH);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_XML);
		httpDelete.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpDelete.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(METHOD_NOT_ALLOWED, response.getStatusLine()
				.getStatusCode());
	}

	@Test
	public void testWrongAcceptTypeDelete() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = new HttpDelete(getServerBasePath()
				+ BaseResource.SLASH + ConfigurationResource.PATH);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_XML);
		httpDelete.addHeader(acceptHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpDelete.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(METHOD_NOT_ALLOWED, response.getStatusLine()
				.getStatusCode());
	}

	@Test
	public void testGetCorrectEntity() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(ConfigurationResource.PATH);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		ConfigurationResponseBody entityResponseBody = gson.fromJson(
				responseBody, ConfigurationResponseBody.class);

		Assert.assertTrue(entityResponseBody.getLoginTokenLifeTime().equals(
				configuration.getLoginTokenLifeTime()));
		Assert.assertTrue(entityResponseBody.getCompanyName().equals(
				configuration.getCompanyName()));
		Assert.assertTrue(entityResponseBody.getBillTemplate().equals(
				configuration.getBillTemplate()));
	}
	
	@Test
	public void testPutMissingLoginTokenLifeTime() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(ConfigurationResource.PATH);

		ConfigurationRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setLoginTokenLifeTime(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMissingCompanyName() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(ConfigurationResource.PATH);

		ConfigurationRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setCompanyName(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMissingBillTemplate() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(ConfigurationResource.PATH);

		ConfigurationRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setBillTemplate(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutCorrectEntity() throws ClientProtocolException,
			IOException {

		// Alter entity
		HttpPut httpPut = generateBasicHttpPut(ConfigurationResource.PATH);

		ConfigurationRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(ConfigurationResource.PATH);

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		ConfigurationResponseBody entityResponseBody = gson.fromJson(
				responseBody, ConfigurationResponseBody.class);

		Assert.assertTrue(entityResponseBody.getLoginTokenLifeTime().equals(
				entityRequestBody.getLoginTokenLifeTime()));
		Assert.assertTrue(entityResponseBody.getCompanyName().equals(
				entityRequestBody.getCompanyName()));
		Assert.assertTrue(entityResponseBody.getBillTemplate().equals(
				entityRequestBody.getBillTemplate()));

	}

	// Utilitary functions
	private ConfigurationRequestBody generateBasicEntityRequestBody() {
		ConfigurationRequestBody entityRequestBody = new ConfigurationRequestBody();

		entityRequestBody.setLoginTokenLifeTime(RandomGenerator.randomPositiveLong());
		entityRequestBody.setCompanyName(RandomGenerator.generateString(80));
		entityRequestBody.setBillTemplate(RandomGenerator.generateString(2000));

		return entityRequestBody;
	}

}
