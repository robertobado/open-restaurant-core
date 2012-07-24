package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.ConsumptionIdentifierResource;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.core.util.StringUtilities;
import net.openrally.restaurant.request.body.ConsumptionIdentifierRequestBody;
import net.openrally.restaurant.response.body.ConsumptionIdentifierResponseBody;

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
public class ConsumptionIdentifierResourceTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Permission permission;
	private ConsumptionIdentifier consumptionIdentifier;
	
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
		consumptionIdentifier = createRandomConsumptionIdentifierAndPersist(company);
	}


	@After
	public void tearDownEntities() {
		
		if(null != consumptionIdentifier){
			consumptionIdentifierDAO.delete(consumptionIdentifier);
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
	
	
	public ConsumptionIdentifierResourceTest() throws Exception {
		super();
	}
	
	@Test
	public void testWrongContentTypePost() throws ClientProtocolException,
			IOException {
		testWrongContentTypePost(ConsumptionIdentifierResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(ConsumptionIdentifierResource.PATH);
	}

	@Test
	public void testInvalidJsonPost() throws ClientProtocolException,
			IOException {
		testInvalidJsonPost(ConsumptionIdentifierResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(ConsumptionIdentifierResource.PATH);
	}

	@Test
	public void testWrongContentTypePut() throws ClientProtocolException,
			IOException {
		testWrongContentTypePut(ConsumptionIdentifierResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePut() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePut(ConsumptionIdentifierResource.PATH);
	}

	@Test
	public void testInvalidJsonPut() throws ClientProtocolException,
			IOException {
		testInvalidJsonPut(ConsumptionIdentifierResource.PATH + BaseResource.SLASH
				+ consumptionIdentifier.getConsumptionIdentifierId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(ConsumptionIdentifierResource.PATH + BaseResource.SLASH
				+ consumptionIdentifier.getConsumptionIdentifierId());
	}

	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException,
			IOException {
		testWrongContentTypeGet(ConsumptionIdentifierResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeGet() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeGet(ConsumptionIdentifierResource.PATH);
	}

	@Test
	public void testWrongContentTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongContentTypeDelete(ConsumptionIdentifierResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeDelete(ConsumptionIdentifierResource.PATH);
	}
	
	@Test
	public void testPostMissingIdentifier() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(ConsumptionIdentifierResource.PATH);

		ConsumptionIdentifierRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setIdentifier(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPostMissingDescription() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(ConsumptionIdentifierResource.PATH);

		ConsumptionIdentifierRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setDescription(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location));

		deleteEntityBasedOnLocation(location, consumptionIdentifierDAO);
	}
	
	@Test
	public void testPostCorrectConsumptionIdentifier() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(ConsumptionIdentifierResource.PATH);

		ConsumptionIdentifierRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location));

		deleteEntityBasedOnLocation(location, consumptionIdentifierDAO);

	}
	
	@Test
	public void testPostDuplicateConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		
		HttpPost httpPost = generateBasicHttpPost(ConsumptionIdentifierResource.PATH);

		ConsumptionIdentifierRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

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

		deleteEntityBasedOnLocation(location, consumptionIdentifierDAO);
	}
	
	@Test
	public void testGetInvalidConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetUnexistingConsumptionIdentifier() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		ConsumptionIdentifierResponseBody consumptionIdentifierResponseBody = gson.fromJson(responseBody,
				ConsumptionIdentifierResponseBody.class);

		Assert.assertEquals(consumptionIdentifierResponseBody.getConsumptionIdentifierId(), consumptionIdentifier.getConsumptionIdentifierId());
		Assert.assertTrue(StringUtils.equals(consumptionIdentifierResponseBody.getIdentifier(), consumptionIdentifier.getIdentifier()));
		Assert.assertTrue(StringUtils.equals(consumptionIdentifierResponseBody.getDescription(), consumptionIdentifier.getDescription()));
	}

	@Test
	public void testGetOtherCompanysConsumptionIdentifier() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpGet httpGet = generateBasicHttpGet(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);

	}
	
	@Test
	public void testGetListCorrectly() throws ClientProtocolException, IOException{
		
		ConsumptionIdentifier entity2 = createRandomConsumptionIdentifierAndPersist(company);
		
		ConsumptionIdentifier entity3 = createRandomConsumptionIdentifierAndPersist(company);	
		
		HttpGet httpGet = generateBasicHttpGet(ConsumptionIdentifierResource.PATH);
		
		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);
		
		Type listType = new TypeToken<List<ConsumptionIdentifierResponseBody>>() {}.getType();
		
		List<ConsumptionIdentifierResponseBody> entityResponseBodyList = gson.fromJson(responseBody,
				listType);
		
		ConsumptionIdentifierResponseBody entityResponseBody1 = new ConsumptionIdentifierResponseBody(consumptionIdentifier);
		ConsumptionIdentifierResponseBody entityResponseBody2 = new ConsumptionIdentifierResponseBody(entity2);
		ConsumptionIdentifierResponseBody entityResponseBody3 = new ConsumptionIdentifierResponseBody(entity3);
		
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		
		consumptionIdentifierDAO.delete(entity2);
		consumptionIdentifierDAO.delete(entity3);
	}
	
	@Test
	public void testPutInvalidConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutUnexistingConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + (consumptionIdentifier.getConsumptionIdentifierId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingIdentifier() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId());

		ConsumptionIdentifierRequestBody consumptionIdentifierRequestBody = generateBasicEntityRequestBody();

		consumptionIdentifierRequestBody.setIdentifier(null);

		String requestBody = getGsonInstance().toJson(consumptionIdentifierRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMissingDescription() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId());

		ConsumptionIdentifierRequestBody consumptionIdentifierRequestBody = generateBasicEntityRequestBody();

		consumptionIdentifierRequestBody.setDescription(null);

		String requestBody = getGsonInstance().toJson(consumptionIdentifierRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutCorrectConsumptionIdentifier() throws ClientProtocolException,
			IOException {

		// Alter entity

		HttpPut httpPut = generateBasicHttpPut(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId());

		ConsumptionIdentifierRequestBody consumptionIdentifierRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(consumptionIdentifierRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());
		
		// Consume response body to release connection for next request
				StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		ConsumptionIdentifierResponseBody consumptionIdentifierResponseBody = gson.fromJson(responseBody,
				ConsumptionIdentifierResponseBody.class);

		Assert.assertEquals(consumptionIdentifierResponseBody.getConsumptionIdentifierId(), consumptionIdentifier.getConsumptionIdentifierId());
		Assert.assertTrue(StringUtils.equals(consumptionIdentifierResponseBody.getIdentifier(), consumptionIdentifierRequestBody.getIdentifier()));
		Assert.assertTrue(StringUtils.equals(consumptionIdentifierResponseBody.getDescription(), consumptionIdentifierRequestBody.getDescription()));
	
	}

	@Test
	public void testPutOtherCompanysConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpPut httpPut = generateBasicHttpPut(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId());
		
		ConsumptionIdentifierRequestBody consumptionIdentifierRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(consumptionIdentifierRequestBody);

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
	public void testDeleteIncorrectConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteUnexistingConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + (consumptionIdentifier.getConsumptionIdentifierId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		consumptionIdentifier = null;
	}

	@Test
	public void testDeleteOtherCompanysConsumptionIdentifier() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpDelete httpDelete = generateBasicHttpDelete(ConsumptionIdentifierResource.PATH
				+ BaseResource.SLASH + consumptionIdentifier.getConsumptionIdentifierId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	// Utilitary functions
	private ConsumptionIdentifierRequestBody generateBasicEntityRequestBody() {
		ConsumptionIdentifierRequestBody consumptionIdentifierRequestBody = new ConsumptionIdentifierRequestBody();

		consumptionIdentifierRequestBody.setIdentifier(RandomGenerator.generateString(10));
		consumptionIdentifierRequestBody.setDescription(RandomGenerator.generateString(50));

		return consumptionIdentifierRequestBody;
	}

}
