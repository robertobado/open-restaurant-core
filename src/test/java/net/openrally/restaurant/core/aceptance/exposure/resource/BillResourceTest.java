package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.BillResource;
import net.openrally.restaurant.core.persistence.entity.Bill;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.BillRequestBody;
import net.openrally.restaurant.core.response.body.BillListResponseBody;
import net.openrally.restaurant.core.response.body.BillResponseBody;
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
public class BillResourceTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Permission permission;
	private ConsumptionIdentifier consumptionIdentifier;
	private ConsumptionIdentifier consumptionIdentifier2;
	private ConsumptionIdentifier consumptionIdentifier3;
	private ConsumptionIdentifier consumptionIdentifier4;
	private Bill bill;
	private Bill bill1;
	private Bill bill2;
	private Bill bill3;
	private Bill bill4;

	public BillResourceTest() throws Exception {
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
		consumptionIdentifier = createRandomConsumptionIdentifierAndPersist(company);
		consumptionIdentifier2 = createRandomConsumptionIdentifierAndPersist(company);
		bill = createOpenBillAndPersist(consumptionIdentifier2);
		
		consumptionIdentifier3 = createRandomConsumptionIdentifierAndPersist(company);

		bill1 = new Bill();
		bill1.setStatus(BillResource.Status.OPEN.toString());
		bill1.setOpenTimestamp(100L);
		bill1.setConsumptionIdentifier(consumptionIdentifier3);
		billDAO.save(bill1);
		
		bill2 = new Bill();
		bill2.setStatus(BillResource.Status.CLOSED.toString());
		bill2.setOpenTimestamp(200L);
		bill2.setCloseTimestamp(300L);
		bill2.setConsumptionIdentifier(consumptionIdentifier3);
		billDAO.save(bill2);

		consumptionIdentifier4 = createRandomConsumptionIdentifierAndPersist(company);
		
		bill3 = new Bill();
		bill3.setStatus(BillResource.Status.CLOSED.toString());
		bill3.setOpenTimestamp(400L);
		bill3.setCloseTimestamp(500L);
		bill3.setConsumptionIdentifier(consumptionIdentifier4);
		billDAO.save(bill3);
		
		bill4 = new Bill();
		bill4.setStatus(BillResource.Status.OPEN.toString());
		bill4.setOpenTimestamp(600L);
		bill4.setConsumptionIdentifier(consumptionIdentifier4);
		billDAO.save(bill4);
		
	}

	@After
	public void tearDownEntities() {

		if (null != bill) {
			billDAO.delete(bill);
		}
		
		billDAO.delete(bill1);
		billDAO.delete(bill2);
		billDAO.delete(bill3);
		billDAO.delete(bill4);

		consumptionIdentifierDAO.delete(consumptionIdentifier);
		consumptionIdentifierDAO.delete(consumptionIdentifier2);
		consumptionIdentifierDAO.delete(consumptionIdentifier3);
		consumptionIdentifierDAO.delete(consumptionIdentifier4);

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
		testWrongContentTypePost(BillResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(BillResource.PATH);
	}

	@Test
	public void testInvalidJsonPost() throws ClientProtocolException,
			IOException {
		testInvalidJsonPost(BillResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(BillResource.PATH);
	}

	@Test
	public void testWrongContentTypePut() throws ClientProtocolException,
			IOException {
		testWrongContentTypePut(BillResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePut() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePut(BillResource.PATH);
	}

	@Test
	public void testInvalidJsonPut() throws ClientProtocolException,
			IOException {
		testInvalidJsonPut(BillResource.PATH + BaseResource.SLASH
				+ bill.getBillId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(BillResource.PATH + BaseResource.SLASH
				+ bill.getBillId());
	}

	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException,
			IOException {
		testWrongContentTypeGet(BillResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeGet() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeGet(BillResource.PATH);
	}

	@Test
	public void testWrongContentTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongContentTypeDelete(BillResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeDelete(BillResource.PATH);
	}

	@Test
	public void testPostMissingConsumptionIdentifier()
			throws ClientProtocolException, IOException {
		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setConsumptionIdentifierId(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPostInvalidStatus()
			throws ClientProtocolException, IOException {
		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setStatus("invalidBillStatus");

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostMissingStatus() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setStatus(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostMissingOpenTimestamp() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setOpenTimestamp(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location));

		deleteEntityBasedOnLocation(location, billDAO);
	}

	@Test
	public void testPostMissingCloseTimestamp() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setCloseTimestamp(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location));

		deleteEntityBasedOnLocation(location, billDAO);
	}

	@Test
	public void testPostCorrectEntity() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location));

		deleteEntityBasedOnLocation(location, billDAO);

	}

	@Test
	public void testPostDuplicateEntity() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

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

		Assert.assertEquals(Status.CONFLICT.getStatusCode(), response
				.getStatusLine().getStatusCode());

		deleteEntityBasedOnLocation(location, billDAO);
	}

	@Test
	public void postBillOnOtherCompanysConsumptionIdentifier()
			throws ClientProtocolException, IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);

		authorizedToken = loginToken.getToken();

		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);

	}

	@Test
	public void testGetInvalidEntity() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetUnexistingEntity() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectEntity() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillResponseBody entityResponseBody = gson.fromJson(responseBody,
				BillResponseBody.class);

		Assert.assertEquals(entityResponseBody.getBillId(), bill.getBillId());
		Assert.assertTrue(StringUtils.equals(entityResponseBody.getStatus(),
				bill.getStatus()));
		Assert.assertEquals(entityResponseBody.getCloseTimestamp(),
				bill.getCloseTimestamp());
		Assert.assertEquals(entityResponseBody.getOpenTimestamp(),
				bill.getOpenTimestamp());
		Assert.assertEquals(entityResponseBody.getConsumptionIdentifierId(),
				bill.getConsumptionIdentifier().getConsumptionIdentifierId());
	}

	@Test
	public void testGetOtherCompanysEntity() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);

		authorizedToken = loginToken.getToken();

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);

	}

	@Test
	public void testGetFullListCorrectly() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 5);
		
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListInvalidStatusParameter() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?status="+BillResource.Status.CLOSED.toString()+"&status=invalidStatus");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testGetListOnlyInvalidStatusParameter() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?status=invalidStatus");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testGetListAllStatus() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?status="+ BillResource.Status.OPEN.toString() +"&status="+ BillResource.Status.CLOSED.toString());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 5);
		
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListOnlyOpen() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?status="+ BillResource.Status.OPEN.toString());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 3);
		
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListOnlyClosed() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?status=" + BillResource.Status.CLOSED.toString());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 2);
		
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListFirstConsumeIdentifier() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?consumptionIdentifierId=" + consumptionIdentifier.getConsumptionIdentifierId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 0);
		
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListSecondConsumeIdentifier() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?consumptionIdentifierId=" + consumptionIdentifier2.getConsumptionIdentifierId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 1);
		
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListThirdConsumeIdentifier() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?consumptionIdentifierId=" + consumptionIdentifier3.getConsumptionIdentifierId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 2);
		
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListFourthConsumeIdentifier() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?consumptionIdentifierId=" + consumptionIdentifier4.getConsumptionIdentifierId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 2);
		
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListStartOpenTimestamp() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?startOpenTimestamp=200");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 4);
		
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListEndOpenTimestamp() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?endOpenTimestamp=600");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 4);
		
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListStartCloseTimestamp() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?startCloseTimestamp=400");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 1);
		
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListEndCloseTimestamp() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?endCloseTimestamp=400");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillListResponseBody entityListResponseBody = gson.fromJson(
				responseBody, BillListResponseBody.class);

		BillResponseBody entityResponseBody = new BillResponseBody(
				bill);
		BillResponseBody entityResponseBody1 = new BillResponseBody(
				bill1);
		BillResponseBody entityResponseBody2 = new BillResponseBody(
				bill2);
		BillResponseBody entityResponseBody3 = new BillResponseBody(
				bill3);
		BillResponseBody entityResponseBody4 = new BillResponseBody(
				bill4);

		List<BillResponseBody> entityResponseBodyList = entityListResponseBody
				.getList();
		
		Assert.assertEquals(entityResponseBodyList.size(), 1);
		
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertFalse(entityResponseBodyList.contains(entityResponseBody4));

	}
	
	@Test
	public void testGetListInvalidStartOpenTimestamp() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?startOpenTimestamp=1y0");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testGetListInvalidEndOpenTimestamp() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?endOpenTimestamp=1y0");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testGetListInvalidStartCloseTimestamp() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?startCloseTimestamp=1y0");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testGetListInvalidEndCloseTimestamp() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?endCloseTimestamp=1y0");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testGetListInvalidConsumptionIdentifierId() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?consumptionIdentifierId=xx2");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testGetListNonExistingConsumptionIdentifierId() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH + "?consumptionIdentifierId=" + consumptionIdentifier3.getConsumptionIdentifierId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testPutInvalidEntity() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(BillResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutUnexistingProduct() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(BillResource.PATH
				+ BaseResource.SLASH + (bill.getBillId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingStatus() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setStatus(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMissingConsumptionIdentifier() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setConsumptionIdentifierId(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutInvalidStatus() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setStatus("invalid-status");

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMoveBillToConsumeIdentifierWithAlreadyOpenBill() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setConsumptionIdentifierId(consumptionIdentifier3.getConsumptionIdentifierId());

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.CONFLICT.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMoveBillToOtherCompanysConsumeIdentifier() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		ConsumptionIdentifier consumptionIdentifier = createRandomConsumptionIdentifierAndPersist(company);

		HttpPut httpPut = generateBasicHttpPut(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setConsumptionIdentifierId(consumptionIdentifier.getConsumptionIdentifierId());

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		consumptionIdentifierDAO.delete(consumptionIdentifier);
		companyDAO.delete(company);
	}

	@Test
	public void testPutCorrectEntity() throws ClientProtocolException,
			IOException {

		// Alter entity

		HttpPut httpPut = generateBasicHttpPut(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());
		
		// Consume response body to release connection for next request
				StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillResponseBody entityResponseBody = gson.fromJson(responseBody,
				BillResponseBody.class);

		Assert.assertEquals(Long.compare(entityResponseBody.getBillId(), bill.getBillId()),0);
		Assert.assertTrue(StringUtils.equals(entityResponseBody.getStatus(), entityRequestBody.getStatus()));
		Assert.assertEquals(Long.compare(entityResponseBody.getConsumptionIdentifierId(), entityRequestBody.getConsumptionIdentifierId()),0);
		Assert.assertEquals(Long.compare(entityResponseBody.getCloseTimestamp(), entityRequestBody.getCloseTimestamp()),0);
		Assert.assertEquals(Long.compare(entityResponseBody.getOpenTimestamp(), entityRequestBody.getOpenTimestamp()),0);
	
	}

	@Test
	public void testPutOtherCompanysEntity() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpPut httpPut = generateBasicHttpPut(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());
		
		BillRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

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
	public void testDeleteIncorrectProduct() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(BillResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteUnexistingProduct() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(BillResource.PATH
				+ BaseResource.SLASH + (bill.getBillId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectProduct() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		bill = null;
	}

	@Test
	public void testDeleteOtherCompanysProduct() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpDelete httpDelete = generateBasicHttpDelete(BillResource.PATH
				+ BaseResource.SLASH + bill.getBillId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}

	// Utilitary functions
	private BillRequestBody generateBasicEntityRequestBody() {
		BillRequestBody entityRequestBody = new BillRequestBody();

		entityRequestBody.setConsumptionIdentifierId(consumptionIdentifier
				.getConsumptionIdentifierId());
		entityRequestBody.setStatus(BillResource.Status.OPEN.toString());
		entityRequestBody.setOpenTimestamp(System.currentTimeMillis() / 1000);
		entityRequestBody
				.setCloseTimestamp(System.currentTimeMillis() / 1000 + 3600);

		return entityRequestBody;
	}
}
