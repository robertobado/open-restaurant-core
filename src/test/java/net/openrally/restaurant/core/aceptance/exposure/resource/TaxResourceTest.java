package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.TaxResource;
import net.openrally.restaurant.core.persistence.entity.Bill;
import net.openrally.restaurant.core.persistence.entity.BillItem;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.Tax;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.core.util.StringUtilities;
import net.openrally.restaurant.request.body.TaxRequestBody;
import net.openrally.restaurant.response.body.TaxResponseBody;

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
public class TaxResourceTest extends BaseResourceTest {
	
	private Company company;
	private Tax tax;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Permission permission;

	public TaxResourceTest() throws Exception {
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
		tax = createRandomTaxAndPersist(company);
	}
	
	@After
	public void tearDownEntities() {

		if (null != tax) {
			taxDAO.delete(tax);
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
		testWrongContentTypePost(TaxResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(TaxResource.PATH);
	}

	@Test
	public void testInvalidJsonPost() throws ClientProtocolException,
			IOException {
		testInvalidJsonPost(TaxResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(TaxResource.PATH);
	}

	@Test
	public void testWrongContentTypePut() throws ClientProtocolException,
			IOException {
		testWrongContentTypePut(TaxResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePut() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePut(TaxResource.PATH);
	}

	@Test
	public void testInvalidJsonPut() throws ClientProtocolException,
			IOException {
		testInvalidJsonPut(TaxResource.PATH + BaseResource.SLASH
				+ tax.getTaxId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(TaxResource.PATH + BaseResource.SLASH
				+ tax.getTaxId());
	}

	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException,
			IOException {
		testWrongContentTypeGet(TaxResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeGet() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeGet(TaxResource.PATH);
	}

	@Test
	public void testWrongContentTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongContentTypeDelete(TaxResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeDelete(TaxResource.PATH);
	}
	
	@Test
	public void testPostMissingName()
			throws ClientProtocolException, IOException {
		HttpPost httpPost = generateBasicHttpPost(TaxResource.PATH);

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setName(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPostMissingDescription()
			throws ClientProtocolException, IOException {
		HttpPost httpPost = generateBasicHttpPost(TaxResource.PATH);

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

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

		deleteEntityBasedOnLocation(location, taxDAO);
	}
	
	@Test
	public void testPostMissingAmount()
			throws ClientProtocolException, IOException {
		HttpPost httpPost = generateBasicHttpPost(TaxResource.PATH);

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setAmount(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostMissingPercentage() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(TaxResource.PATH);

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setPercentage(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPostCorrectEntity() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(TaxResource.PATH);

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

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

		deleteEntityBasedOnLocation(location, taxDAO);

	}
	
	@Test
	public void testPostDuplicateEntity() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(TaxResource.PATH);

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

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

		deleteEntityBasedOnLocation(location, taxDAO);
	}
	
	@Test
	public void postTaxOnOtherCompany()
			throws ClientProtocolException, IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);

		authorizedToken = loginToken.getToken();

		HttpPost httpPost = generateBasicHttpPost(TaxResource.PATH);

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

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
		HttpGet httpGet = generateBasicHttpGet(TaxResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetUnexistingEntity() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectEntity() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		TaxResponseBody entityResponseBody = gson.fromJson(responseBody,
				TaxResponseBody.class);

		Assert.assertTrue(entityResponseBody.getTaxId().equals(tax.getTaxId()));
		Assert.assertTrue(entityResponseBody.getName().equals(tax.getName()));
		Assert.assertTrue(entityResponseBody.getDescription().equals(tax.getDescription()));
		Assert.assertTrue(entityResponseBody.getAmount().equals(tax.getAmount()));
		Assert.assertTrue(entityResponseBody.getPercentage().equals(tax.getPercentage()));
		
	}

	@Test
	public void testGetOtherCompanysEntity() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);

		authorizedToken = loginToken.getToken();

		HttpGet httpGet = generateBasicHttpGet(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

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
		
		Tax tax2 = createRandomTaxAndPersist(company);
				
		Tax tax3 = createRandomTaxAndPersist(company);

		HttpGet httpGet = generateBasicHttpGet(TaxResource.PATH);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		Type listType = new TypeToken<List<TaxResponseBody>>() {}.getType();
		
		List<TaxResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);

		TaxResponseBody entityResponseBody = new TaxResponseBody(
				tax);
		TaxResponseBody entityResponseBody1 = new TaxResponseBody(
				tax2);
		TaxResponseBody entityResponseBody2 = new TaxResponseBody(
				tax3);
		
		Assert.assertEquals(entityResponseBodyList.size(), 3);
		
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		
		taxDAO.delete(tax3);
		taxDAO.delete(tax2);
	}
	
	@Test
	public void testPutInvalidEntity() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(TaxResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutUnexistingEntity() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(TaxResource.PATH
				+ BaseResource.SLASH + (tax.getTaxId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingName() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setName(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMissingDescription() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setDescription(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());
	}
	
	@Test
	public void testPutMissingAmount() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setAmount(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMissingPercentage() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setPercentage(null);

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
		HttpPut httpPut = generateBasicHttpPut(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());
		
		// Consume response body to release connection for next request
				StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		TaxResponseBody entityResponseBody = gson.fromJson(responseBody,
				TaxResponseBody.class);
		
		Assert.assertTrue(entityResponseBody.getTaxId().equals(tax.getTaxId()));
		Assert.assertTrue(entityResponseBody.getName().equals(entityRequestBody.getName()));
		Assert.assertTrue(entityResponseBody.getDescription().equals(entityRequestBody.getDescription()));
		Assert.assertTrue(entityResponseBody.getAmount().equals(entityRequestBody.getAmount()));
		Assert.assertTrue(entityResponseBody.getPercentage().equals(entityRequestBody.getPercentage()));
		
		}

	@Test
	public void testPutOtherCompanysEntity() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpPut httpPut = generateBasicHttpPut(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());
		
		TaxRequestBody entityRequestBody = generateBasicEntityRequestBody();

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
		HttpDelete httpDelete = generateBasicHttpDelete(TaxResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteUnexistingEntity() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(TaxResource.PATH
				+ BaseResource.SLASH + (tax.getTaxId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectEntity() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		tax = null;
	}

	@Test
	public void testDeleteOtherCompanysEntity() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpDelete httpDelete = generateBasicHttpDelete(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	@Test
	public void testDeleteTaxAssociatedWithBillItem() throws ClientProtocolException, IOException{
		ConsumptionIdentifier consumptionIdentifier = createRandomConsumptionIdentifierAndPersist(company);
		Bill bill = createOpenBillAndPersist(consumptionIdentifier);
		BillItem billItem = new BillItem();
		billItem.setBill(bill);
		billItem.setQuantity(3.0);
		billItem.setReferenceId(tax.getTaxId());
		billItem.setUnitPrice(tax.getAmount());
		billItem.setType(BillItem.Type.TAX.toString());
		billItemDAO.save(billItem);
		
		HttpDelete httpDelete = generateBasicHttpDelete(TaxResource.PATH
				+ BaseResource.SLASH + tax.getTaxId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.CONFLICT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		billItemDAO.delete(billItem);
		billDAO.delete(bill);
		consumptionIdentifierDAO.delete(consumptionIdentifier);
	}
	
	// Utilitary functions
	private TaxRequestBody generateBasicEntityRequestBody() {
		TaxRequestBody entityRequestBody = new TaxRequestBody();

		entityRequestBody.setName(RandomGenerator.generateString(20));
		entityRequestBody.setDescription(RandomGenerator.generateString(200));
		entityRequestBody.setAmount(RandomGenerator.randomDouble(80));
		entityRequestBody.setPercentage(RandomGenerator.randomBoolean());

		return entityRequestBody;
	}

}
