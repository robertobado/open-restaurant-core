package net.openrally.restaurant.core.aceptance.exposure.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.BillItemResource;
import net.openrally.restaurant.core.exposure.resource.BillResource;
import net.openrally.restaurant.core.persistence.entity.BillItem;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Product;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.Tax;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.StringUtilities;
import net.openrally.restaurant.request.body.BillItemRequestBody;
import net.openrally.restaurant.request.body.BillRequestBody;
import net.openrally.restaurant.response.body.BillItemResponseBody;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
public class TaxingTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Permission permission;
	private ConsumptionIdentifier consumptionIdentifier;
	private Product product;

	public TaxingTest() throws Exception {
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
		product = createRandomProductAndPersist(company);
		product.setPrice(2.75);
		productDAO.update(product);
	}

	@After
	public void tearDownEntities() {

		productDAO.delete(product);

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
	public void testShouldAddFixedTaxItemToBill()
			throws ClientProtocolException, IOException {
		Double amount = 10.0;

		Tax tax = new Tax();
		tax.setCompany(company);
		tax.setName("test");
		tax.setPercentage(false);
		tax.setAmount(amount);

		taxDAO.save(tax);

		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicBillRequestBody();

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

		String entityIdString = location
				.substring(location.lastIndexOf("/") + 1);

		Long billId = Long.parseLong(entityIdString);

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ "?billId=" + billId);

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		Type listType = new TypeToken<List<BillItemResponseBody>>() {
		}.getType();

		List<BillItemResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);

		Assert.assertEquals(1, entityResponseBodyList.size());

		BillItemResponseBody billItem = entityResponseBodyList.get(0);

		assertTrue(StringUtils.equals(billItem.getType(),
				BillItem.Type.TAX.toString()));
		assertEquals(amount, billItem.getUnitPrice());
		assertTrue(billItem.getQuantity().equals(1.0));

		billItemDAO.deleteById(billItem.getBillItemId());

		deleteEntityBasedOnLocation(location, billDAO);

		taxDAO.delete(tax);
	}

	@Test
	public void testShouldAddPercentageTaxItemToBill()
			throws ClientProtocolException, IOException {
		Double amount = 10.0;

		Tax tax = new Tax();
		tax.setCompany(company);
		tax.setName("test");
		tax.setPercentage(true);
		tax.setAmount(amount);

		taxDAO.save(tax);

		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicBillRequestBody();

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

		String entityIdString = location
				.substring(location.lastIndexOf("/") + 1);

		Long billId = Long.parseLong(entityIdString);

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ "?billId=" + billId);

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		Type listType = new TypeToken<List<BillItemResponseBody>>() {
		}.getType();

		List<BillItemResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);

		Assert.assertEquals(1, entityResponseBodyList.size());

		BillItemResponseBody billItem = entityResponseBodyList.get(0);

		assertTrue(StringUtils.equals(billItem.getType(),
				BillItem.Type.TAX.toString()));
		assertTrue(billItem.getUnitPrice().equals(0.0));
		assertTrue(billItem.getQuantity().equals(1.0));

		billItemDAO.deleteById(billItem.getBillItemId());

		deleteEntityBasedOnLocation(location, billDAO);

		taxDAO.delete(tax);
	}

	@Test
	public void testFixedTaxItemAmountShouldNotChange()
			throws ClientProtocolException, IOException {
		Double amount = 15.0;

		Tax tax = new Tax();
		tax.setCompany(company);
		tax.setName("test");
		tax.setPercentage(false);
		tax.setAmount(amount);

		taxDAO.save(tax);

		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody billRequestBody = generateBasicBillRequestBody();

		String requestBody = getGsonInstance().toJson(billRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String billLocation = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(billLocation));

		String entityIdString = billLocation.substring(billLocation
				.lastIndexOf("/") + 1);

		Long billId = Long.parseLong(entityIdString);

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		// Creates a bill Item
		httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody billItemRequestBody = generateBasicBillItemRequestBody(billId);

		requestBody = getGsonInstance().toJson(billItemRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String billItemLocation = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(billItemLocation));

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ "?billId=" + billId);

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		Type listType = new TypeToken<List<BillItemResponseBody>>() {
		}.getType();

		List<BillItemResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);

		Assert.assertEquals(2, entityResponseBodyList.size());

		BillItemResponseBody billItem = null;

		for (BillItemResponseBody billItemResponseBody : entityResponseBodyList) {
			if (!StringUtils.equals(BillItem.Type.TAX.toString(),
					billItemResponseBody.getType())) {
				continue;
			}
			billItem = billItemResponseBody;
		}

		assertNotNull(billItem);

		assertTrue(StringUtils.equals(billItem.getType(),
				BillItem.Type.TAX.toString()));
		assertEquals(amount, billItem.getUnitPrice());
		assertTrue(billItem.getQuantity().equals(1.0));

		billItemDAO.deleteById(billItem.getBillItemId());

		deleteEntityBasedOnLocation(billItemLocation, billItemDAO);

		deleteEntityBasedOnLocation(billLocation, billDAO);

		taxDAO.delete(tax);
	}

	@Test
	public void testPercentageTaxItemAmountShouldUpdate()
			throws ClientProtocolException, IOException {
		Double amount = 15.0;

		Tax tax = new Tax();
		tax.setCompany(company);
		tax.setName("test");
		tax.setPercentage(true);
		tax.setAmount(amount);

		taxDAO.save(tax);

		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody billRequestBody = generateBasicBillRequestBody();

		String requestBody = getGsonInstance().toJson(billRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		Header locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String billLocation = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(billLocation));

		String entityIdString = billLocation.substring(billLocation
				.lastIndexOf("/") + 1);

		Long billId = Long.parseLong(entityIdString);

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		// Creates a bill Item
		httpPost = generateBasicHttpPost(BillItemResource.PATH);

		BillItemRequestBody billItemRequestBody = generateBasicBillItemRequestBody(billId);

		billItemRequestBody.setReferenceId(product.getProductId());

		requestBody = getGsonInstance().toJson(billItemRequestBody);

		httpPost.setEntity(new StringEntity(requestBody));

		response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());

		locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String billItemLocation = locationHeader.getValue();

		Assert.assertFalse(StringUtils.isBlank(billItemLocation));

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		HttpGet httpGet = generateBasicHttpGet(BillItemResource.PATH
				+ "?billId=" + billId);

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		Type listType = new TypeToken<List<BillItemResponseBody>>() {
		}.getType();

		List<BillItemResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);

		Assert.assertEquals(2, entityResponseBodyList.size());

		BillItemResponseBody billItem = null;

		for (BillItemResponseBody billItemResponseBody : entityResponseBodyList) {
			if (!StringUtils.equals(BillItem.Type.TAX.toString(),
					billItemResponseBody.getType())) {
				continue;
			}
			billItem = billItemResponseBody;
		}

		assertNotNull(billItem);

		assertTrue(StringUtils.equals(billItem.getType(),
				BillItem.Type.TAX.toString()));
		assertTrue(billItem.getUnitPrice().equals(
				(amount * billItemRequestBody.getQuantity()
						* product.getPrice() / 100)));
		assertTrue(billItem.getQuantity().equals(1.0));

		billItemDAO.deleteById(billItem.getBillItemId());

		deleteEntityBasedOnLocation(billItemLocation, billItemDAO);

		deleteEntityBasedOnLocation(billLocation, billDAO);

		taxDAO.delete(tax);
	}

	@Test
	public void testShouldDeleteBillCorrectly() throws ClientProtocolException,
			IOException {
		Double amount = 10.0;

		Tax fixedTax = new Tax();

		fixedTax.setCompany(company);
		fixedTax.setName("test-fixed");
		fixedTax.setPercentage(false);
		fixedTax.setAmount(amount);

		taxDAO.save(fixedTax);

		Tax percentageTax = new Tax();

		percentageTax.setCompany(company);
		percentageTax.setName("test-percentage");
		percentageTax.setPercentage(true);
		percentageTax.setAmount(amount);

		taxDAO.save(percentageTax);

		HttpPost httpPost = generateBasicHttpPost(BillResource.PATH);

		BillRequestBody entityRequestBody = generateBasicBillRequestBody();

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

		String entityIdString = location
				.substring(location.lastIndexOf("/") + 1);

		Long billId = Long.parseLong(entityIdString);

		// Consume response body to release connection for next request
		StringUtilities.httpResponseAsString(response);

		HttpDelete httpDelete = generateBasicHttpDelete(BillResource.PATH
				+ BaseResource.SLASH + billId);

		response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());

		taxDAO.delete(fixedTax);

		taxDAO.delete(percentageTax);
	}

	// Utilitary functions
	private BillRequestBody generateBasicBillRequestBody() {
		BillRequestBody entityRequestBody = new BillRequestBody();

		entityRequestBody.setConsumptionIdentifierId(consumptionIdentifier
				.getConsumptionIdentifierId());
		entityRequestBody.setStatus(BillResource.Status.OPEN.toString());
		entityRequestBody.setOpenTimestamp(System.currentTimeMillis() / 1000);
		entityRequestBody
				.setCloseTimestamp(System.currentTimeMillis() / 1000 + 3600);

		return entityRequestBody;
	}

	private BillItemRequestBody generateBasicBillItemRequestBody(Long billId) {
		BillItemRequestBody entityRequestBody = new BillItemRequestBody();

		entityRequestBody.setBillId(billId);
		entityRequestBody.setReferenceId(product.getProductId());
		entityRequestBody.setQuantity(3.0);

		return entityRequestBody;
	}

}
