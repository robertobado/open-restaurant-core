package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.BillItemResource;
import net.openrally.restaurant.core.exposure.resource.BillResource;
import net.openrally.restaurant.core.persistence.entity.Bill;
import net.openrally.restaurant.core.persistence.entity.BillItem;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Product;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.core.util.StringUtilities;
import net.openrally.restaurant.request.body.BillItemRequestBody;
import net.openrally.restaurant.response.body.BillItemResponseBody;

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
public class BillItemResourceTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Permission permission;
	private ConsumptionIdentifier consumptionIdentifier;
	private Bill bill;
	private Product product;
	private BillItem billItem;

	public BillItemResourceTest() throws Exception {
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
		bill = createOpenBillAndPersist(consumptionIdentifier);
		product = createRandomProductAndPersist(company);
		billItem = createRandomProductBillItemAndPersist(bill, product);
	}

	@After
	public void tearDownEntities() {

		if (null != billItem) {
			billItemDAO.delete(billItem);
		}

		productDAO.delete(product);

		billDAO.delete(bill);

		consumptionIdentifierDAO.delete(consumptionIdentifier);

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
		testWrongContentTypePost(BillItemResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(BillItemResource.PATH);
	}

	@Test
	public void testInvalidJsonPost() throws ClientProtocolException,
			IOException {
		testInvalidJsonPost(BillItemResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(BillItemResource.PATH);
	}

	@Test
	public void testWrongContentTypePut() throws ClientProtocolException,
			IOException {
		testWrongContentTypePut(BillItemResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePut() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePut(BillItemResource.PATH);
	}

	@Test
	public void testInvalidJsonPut() throws ClientProtocolException,
			IOException {
		testInvalidJsonPut(BillItemResource.PATH + BaseResource.SLASH
				+ billItem.getBillItemId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(BillItemResource.PATH + BaseResource.SLASH
				+ billItem.getBillItemId());
	}

	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException,
			IOException {
		testWrongContentTypeGet(BillItemResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeGet() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeGet(BillItemResource.PATH);
	}

	@Test
	public void testWrongContentTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongContentTypeDelete(BillItemResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeDelete(BillItemResource.PATH);
	}

	@Test
	public void testPostMissingBillId() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setBillId(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostMissingReferenceId() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setReferenceId(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostNegativeQuantity() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setQuantity(-12.34);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostMissingQuantity() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setQuantity(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostOnClosedBill() throws ClientProtocolException,
			IOException {

		bill.setStatus(BillResource.Status.CLOSED.toString());
		billDAO.update(bill);

		HttpPost httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CONFLICT.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostCorrectEntity() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

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

		deleteEntityBasedOnLocation(location, billItemDAO);

	}

	@Test
	public void testPostDuplicateEntity() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location1 = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location1));

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location2 = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(location2));

		deleteEntityBasedOnLocation(location1, billItemDAO);
		deleteEntityBasedOnLocation(location2, billItemDAO);
	}

	@Test
	public void postBillItemOnOtherCompanysBill()
			throws ClientProtocolException, IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);

		authorizedToken = loginToken.getToken();

		HttpPost httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

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
		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetUnexistingEntity() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectEntity() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillItemResponseBody entityResponseBody = gson.fromJson(responseBody,
				BillItemResponseBody.class);

		Assert.assertTrue(entityResponseBody.getBillItemId().equals(
				billItem.getBillItemId()));
		Assert.assertTrue(entityResponseBody.getBillId().equals(
				billItem.getBill().getBillId()));
		Assert.assertTrue(entityResponseBody.getReferenceId().equals(
				billItem.getReferenceId()));
		Assert.assertTrue(entityResponseBody.getQuantity().equals(
				billItem.getQuantity()));
		Assert.assertTrue(entityResponseBody.getUnitPrice().equals(
				billItem.getUnitPrice()));

	}

	@Test
	public void testGetOtherCompanysEntity() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);

		authorizedToken = loginToken.getToken();

		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);

	}

	@Test
	public void testGetFullListWithInvalidBillId()
			throws ClientProtocolException, IOException {
		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ BaseResource.SLASH + "?billId=xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetFullListWithNonExistingBillId()
			throws ClientProtocolException, IOException {
		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ BaseResource.SLASH + "?billId=" + bill.getBillId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetFullListByBillIdCorrectly()
			throws ClientProtocolException, IOException {

		// create other bill B
		Bill billB = createOpenBillAndPersist(consumptionIdentifier);
		Product productB = createRandomProductAndPersist(company);
		BillItem billItemB = createRandomProductBillItemAndPersist(billB,
				productB);
		billB.setStatus(BillResource.Status.CLOSED.toString());
		billDAO.update(billB);

		Product product2 = createRandomProductAndPersist(company);
		BillItem billItem2 = createRandomProductBillItemAndPersist(bill,
				product2);

		Product product3 = createRandomProductAndPersist(company);
		BillItem billItem3 = createRandomProductBillItemAndPersist(bill,
				product3);

		Product product4 = createRandomProductAndPersist(company);
		BillItem billItem4 = createRandomProductBillItemAndPersist(bill,
				product4);

		Product product5 = createRandomProductAndPersist(company);
		BillItem billItem5 = createRandomProductBillItemAndPersist(bill,
				product5);

		bill.setStatus(BillResource.Status.CLOSED.toString());
		billDAO.update(bill);

		// create other bill C
		Bill billC = createOpenBillAndPersist(consumptionIdentifier);
		Product productC = createRandomProductAndPersist(company);
		BillItem billItemC = createRandomProductBillItemAndPersist(billC,
				productC);
		billC.setStatus(BillResource.Status.CLOSED.toString());
		billDAO.update(billC);

		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ "?billId=" + bill.getBillId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		Type listType = new TypeToken<List<BillItemResponseBody>>() {
		}.getType();

		List<BillItemResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);

		BillItemResponseBody entityResponseBody = new BillItemResponseBody(
				billItem);
		BillItemResponseBody entityResponseBody1 = new BillItemResponseBody(
				billItem2);
		BillItemResponseBody entityResponseBody2 = new BillItemResponseBody(
				billItem3);
		BillItemResponseBody entityResponseBody3 = new BillItemResponseBody(
				billItem4);
		BillItemResponseBody entityResponseBody4 = new BillItemResponseBody(
				billItem5);

		Assert.assertEquals(entityResponseBodyList.size(), 5);

		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody4));

		billItemDAO.delete(billItemB);
		productDAO.delete(productB);
		billDAO.delete(billB);

		billItemDAO.delete(billItem2);
		billItemDAO.delete(billItem3);
		billItemDAO.delete(billItem4);
		billItemDAO.delete(billItem5);

		billItemDAO.delete(billItemC);
		productDAO.delete(productC);
		billDAO.delete(billC);

		productDAO.delete(product2);
		productDAO.delete(product3);
		productDAO.delete(product4);
		productDAO.delete(product5);
	}
	
	@Test
	public void testGetOrderedListByBillIdCorrectly()
			throws ClientProtocolException, IOException {
		
		BillItem billItemB = createRandomProductBillItemAndPersist(bill,
				product);
		
		BillItem billItemC = createRandomProductBillItemAndPersist(bill,
				product);
		
		BillItem billItemD = createRandomProductBillItemAndPersist(bill,
				product);
		
		// simulates a deletion
		billItemDAO.delete(billItemB);
		
		// simulates an update
		billItemC.setQuantity(billItemC.getQuantity() * 2);		
		billItemDAO.update(billItemC);

		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ "?billId=" + bill.getBillId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		Type listType = new TypeToken<List<BillItemResponseBody>>() {
		}.getType();

		List<BillItemResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);

		BillItemResponseBody entityResponseBody = new BillItemResponseBody(
				billItem);
		BillItemResponseBody entityResponseBodyC = new BillItemResponseBody(
				billItemC);
		BillItemResponseBody entityResponseBodyD = new BillItemResponseBody(
				billItemD);

		Assert.assertEquals(entityResponseBodyList.size(), 3);
		
		entityResponseBodyList.get(0).equals(entityResponseBody);
		entityResponseBodyList.get(1).equals(entityResponseBodyC);
		entityResponseBodyList.get(2).equals(entityResponseBodyD);

		billItemDAO.delete(billItemC);
		billItemDAO.delete(billItemD);
	}

	@Test
	public void testPutInvalidEntity() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutUnexistingEntity() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + (billItem.getBillItemId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingBillId() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setBillId(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingProductId() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setReferenceId(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingQuantity() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setQuantity(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMoveBillItemToAClosedBill()
			throws ClientProtocolException, IOException {
		ConsumptionIdentifier consumptionIdentifier2 = createRandomConsumptionIdentifierAndPersist(company);
		Bill bill2 = createOpenBillAndPersist(consumptionIdentifier2);
		bill2.setStatus(BillResource.Status.CLOSED.toString());
		billDAO.update(bill2);

		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setBillId(bill2.getBillId());

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.CONFLICT.getStatusCode(), response
				.getStatusLine().getStatusCode());

		billDAO.delete(bill2);
		consumptionIdentifierDAO.delete(consumptionIdentifier2);
	}

	@Test
	public void testPutMoveBillItemToOtherCompanysBill()
			throws ClientProtocolException, IOException {

		Company company = createCompanyAndPersist();
		ConsumptionIdentifier consumptionIdentifier = createRandomConsumptionIdentifierAndPersist(company);
		Bill bill = createOpenBillAndPersist(consumptionIdentifier);

		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setBillId(bill.getBillId());

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		billDAO.delete(bill);
		consumptionIdentifierDAO.delete(consumptionIdentifier);
		companyDAO.delete(company);
	}

	@Test
	public void testPutTaxBillItem() throws ClientProtocolException,
			IOException {
		
		billItem.setType(BillItem.Type.TAX.toString());
		billItemDAO.update(billItem);

		// Alter entity

		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setReferenceId(product.getProductId());

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}

	@Test
	public void testPutCorrectEntity() throws ClientProtocolException,
			IOException {

		Product product = createRandomProductAndPersist(company);

		// Alter entity

		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setReferenceId(product.getProductId());

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		BillItemResponseBody entityResponseBody = gson.fromJson(responseBody,
				BillItemResponseBody.class);

		Assert.assertTrue(entityResponseBody.getBillItemId().equals(
				billItem.getBillItemId()));
		Assert.assertTrue(entityResponseBody.getBillId().equals(
				entityRequestBody.getBillId()));
		Assert.assertTrue(entityResponseBody.getReferenceId().equals(
				entityRequestBody.getReferenceId()));
		Assert.assertTrue(entityResponseBody.getQuantity().equals(
				entityRequestBody.getQuantity()));
		Assert.assertTrue(entityResponseBody.getUnitPrice().equals(
				product.getPrice()));

		billItemDAO.delete(billItem);
		billItem = null;

		productDAO.delete(product);
	}

	@Test
	public void testPutOtherCompanysEntity() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);

		authorizedToken = loginToken.getToken();

		HttpPut httpPut = generateBasicHttpPut(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		BillItemRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

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
	public void testDeleteIncorrectEntity() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(BillItemResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteUnexistingEntity() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(BillItemResource.PATH
				+ BaseResource.SLASH + (billItem.getBillItemId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectEntity() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());

		billItem = null;
	}

	@Test
	public void testDeleteOtherCompanysEntity() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);

		authorizedToken = loginToken.getToken();

		HttpDelete httpDelete = generateBasicHttpDelete(BillItemResource.PATH
				+ BaseResource.SLASH + billItem.getBillItemId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}

	// Utilitary functions
	private BillItemRequestBody generateBasicEntityRequestBody() {
		BillItemRequestBody entityRequestBody = new BillItemRequestBody();

		entityRequestBody.setBillId(bill.getBillId());
		entityRequestBody.setReferenceId(product.getProductId());
		entityRequestBody
				.setQuantity(RandomGenerator.randomPositiveDouble(100));

		return entityRequestBody;
	}

}
