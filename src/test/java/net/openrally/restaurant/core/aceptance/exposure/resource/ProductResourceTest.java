package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.ProductResource;
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
import net.openrally.restaurant.request.body.ProductRequestBody;
import net.openrally.restaurant.response.body.ProductResponseBody;

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
public class ProductResourceTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;
	private Permission permission;
	private Product product;
	
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
		product = createRandomProductAndPersist(company);
	}


	@After
	public void tearDownEntities() {
		
		if(null != product){
			productDAO.delete(product);
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
	
	
	public ProductResourceTest() throws Exception {
		super();
	}
	
	@Test
	public void testWrongContentTypePost() throws ClientProtocolException,
			IOException {
		testWrongContentTypePost(ProductResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(ProductResource.PATH);
	}

	@Test
	public void testInvalidJsonPost() throws ClientProtocolException,
			IOException {
		testInvalidJsonPost(ProductResource.PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(ProductResource.PATH);
	}

	@Test
	public void testWrongContentTypePut() throws ClientProtocolException,
			IOException {
		testWrongContentTypePut(ProductResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePut() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePut(ProductResource.PATH);
	}

	@Test
	public void testInvalidJsonPut() throws ClientProtocolException,
			IOException {
		testInvalidJsonPut(ProductResource.PATH + BaseResource.SLASH
				+ product.getProductId());
	}

	@Test
	public void testEmptyRequestBodyPut() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPut(ProductResource.PATH + BaseResource.SLASH
				+ product.getProductId());
	}

	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException,
			IOException {
		testWrongContentTypeGet(ProductResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeGet() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeGet(ProductResource.PATH);
	}

	@Test
	public void testWrongContentTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongContentTypeDelete(ProductResource.PATH);
	}

	@Test
	public void testWrongAcceptTypeDelete() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypeDelete(ProductResource.PATH);
	}
	
	@Test
	public void testPostMissingName() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(ProductResource.PATH);

		ProductRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setName(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPostMissingDescription() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(ProductResource.PATH);

		ProductRequestBody entityRequestBody = generateBasicEntityRequestBody();

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

		deleteEntityBasedOnLocation(location, productDAO);
	}
	
	@Test
	public void testPostMissingPrice() throws ClientProtocolException,
			IOException {
		HttpPost httpPost = generateBasicHttpPost(ProductResource.PATH);

		ProductRequestBody entityRequestBody = generateBasicEntityRequestBody();

		entityRequestBody.setPrice(null);

		String requestBody = getGsonInstance().toJson(entityRequestBody);

		httpPost.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPostCorrectProduct() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(ProductResource.PATH);

		ProductRequestBody entityRequestBody = generateBasicEntityRequestBody();

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

		deleteEntityBasedOnLocation(location, productDAO);

	}
	
	@Test
	public void testPostDuplicateProduct() throws ClientProtocolException,
			IOException {
		
		HttpPost httpPost = generateBasicHttpPost(ProductResource.PATH);

		ProductRequestBody entityRequestBody = generateBasicEntityRequestBody();

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

		Assert.assertEquals(Status.CREATED.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		locationHeader = response
				.getLastHeader(BaseResource.LOCATION_HEADER_PARAMETER_NAME);

		Assert.assertNotNull(locationHeader);

		String location2 = locationHeader.getValue();

		deleteEntityBasedOnLocation(location, productDAO);
		deleteEntityBasedOnLocation(location2, productDAO);
	}
	
	@Test
	public void testGetInvalidProduct() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(ProductResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetUnexistingProduct() throws ClientProtocolException,
			IOException {

		HttpGet httpGet = generateBasicHttpGet(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId() + 99);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testGetCorrectProduct() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = generateBasicHttpGet(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		ProductResponseBody productResponseBody = gson.fromJson(responseBody,
				ProductResponseBody.class);

		Assert.assertEquals(productResponseBody.getProductId(), product.getProductId());
		Assert.assertTrue(StringUtils.equals(productResponseBody.getName(), product.getName()));
		Assert.assertTrue(StringUtils.equals(productResponseBody.getDescription(), product.getDescription()));
		Assert.assertTrue(Double.compare(productResponseBody.getPrice(), product.getPrice()) == 0);
	}

	@Test
	public void testGetOtherCompanysProduct() throws ClientProtocolException,
			IOException {

		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpGet httpGet = generateBasicHttpGet(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

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
		
		Product entity2 = createRandomProductAndPersist(company);
		Product entity3 = createRandomProductAndPersist(company);	
		
		HttpGet httpGet = generateBasicHttpGet(ProductResource.PATH);
		
		HttpResponse response = getHttpClient().execute(httpGet);
		
		productDAO.delete(entity2);
		productDAO.delete(entity3);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		Type listType = new TypeToken<List<ProductResponseBody>>() {}.getType();
		
		List<ProductResponseBody> entityResponseBodyList = gson.fromJson(
				responseBody, listType);
		
		ProductResponseBody entityResponseBody1 = new ProductResponseBody(product);
		ProductResponseBody entityResponseBody2 = new ProductResponseBody(entity2);
		ProductResponseBody entityResponseBody3 = new ProductResponseBody(entity3);
		
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody1));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody2));
		Assert.assertTrue(entityResponseBodyList.contains(entityResponseBody3));
		
		
	}
	
	@Test
	public void testPutInvalidProduct() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(ProductResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutUnexistingProduct() throws ClientProtocolException,
			IOException {
		HttpPut httpPut = generateBasicHttpPut(ProductResource.PATH
				+ BaseResource.SLASH + (product.getProductId() + 99));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutMissingName() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

		ProductRequestBody productRequestBody = generateBasicEntityRequestBody();

		productRequestBody.setName(null);

		String requestBody = getGsonInstance().toJson(productRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMissingDescription() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

		ProductRequestBody productRequestBody = generateBasicEntityRequestBody();

		productRequestBody.setDescription(null);

		String requestBody = getGsonInstance().toJson(productRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testPutMissingPrice() throws ClientProtocolException,
			IOException {

		HttpPut httpPut = generateBasicHttpPut(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

		ProductRequestBody productRequestBody = generateBasicEntityRequestBody();

		productRequestBody.setPrice(null);

		String requestBody = getGsonInstance().toJson(productRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testPutCorrectProduct() throws ClientProtocolException,
			IOException {

		// Alter entity

		HttpPut httpPut = generateBasicHttpPut(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

		ProductRequestBody productRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(productRequestBody);

		httpPut.setEntity(new StringEntity(requestBody, UTF_8));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());
		
		// Consume response body to release connection for next request
				StringUtilities.httpResponseAsString(response);

		// Retrieve entity for comparison

		HttpGet httpGet = generateBasicHttpGet(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

		response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusLine()
				.getStatusCode());

		String responseBody = StringUtilities.httpResponseAsString(response);

		ProductResponseBody productResponseBody = gson.fromJson(responseBody,
				ProductResponseBody.class);

		Assert.assertEquals(productResponseBody.getProductId(), product.getProductId());
		Assert.assertTrue(StringUtils.equals(productResponseBody.getName(), productRequestBody.getName()));
		Assert.assertTrue(StringUtils.equals(productResponseBody.getDescription(), productRequestBody.getDescription()));
		Assert.assertTrue(Double.compare(productResponseBody.getPrice(), productRequestBody.getPrice()) == 0);
	}

	@Test
	public void testPutOtherCompanysProduct() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpPut httpPut = generateBasicHttpPut(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());
		
		ProductRequestBody productRequestBody = generateBasicEntityRequestBody();

		String requestBody = getGsonInstance().toJson(productRequestBody);

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
	public void testDeleteIncorrectProduct() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(ProductResource.PATH
				+ BaseResource.SLASH + "xpto");

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteUnexistingProduct() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(ProductResource.PATH
				+ BaseResource.SLASH + (product.getProductId() + 99));

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testDeleteCorrectProduct() throws ClientProtocolException,
			IOException {
		HttpDelete httpDelete = generateBasicHttpDelete(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		product = null;
	}

	@Test
	public void testDeleteOtherCompanysProduct() throws ClientProtocolException,
			IOException {
		
		Company company = createCompanyAndPersist();
		Configuration configuration = createConfigurationAndPersist(company);
		User user = createUserAndPersist(configuration);
		LoginToken loginToken = createLoginTokenAndPersist(user);
		
		authorizedToken = loginToken.getToken();

		HttpDelete httpDelete = generateBasicHttpDelete(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}
	
	@Test
	public void testDeleteProductAssociatedWithBillItem() throws ClientProtocolException, IOException{
		ConsumptionIdentifier consumptionIdentifier = createRandomConsumptionIdentifierAndPersist(company);
		Bill bill = createOpenBillAndPersist(consumptionIdentifier);
		BillItem billItem = new BillItem();
		billItem.setBill(bill);
		billItem.setQuantity(3.0);
		billItem.setReferenceId(product.getProductId());
		billItem.setUnitPrice(product.getPrice());
		billItem.setType(BillItem.Type.PRODUCT.toString());
		billItemDAO.save(billItem);
		
		HttpDelete httpDelete = generateBasicHttpDelete(ProductResource.PATH
				+ BaseResource.SLASH + product.getProductId());

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.CONFLICT.getStatusCode(), response
				.getStatusLine().getStatusCode());
		
		billItemDAO.delete(billItem);
		billDAO.delete(bill);
		consumptionIdentifierDAO.delete(consumptionIdentifier);
	}
	
	// Utilitary functions
	private ProductRequestBody generateBasicEntityRequestBody() {
		ProductRequestBody productRequestBody = new ProductRequestBody();

		productRequestBody.setName(RandomGenerator.generateString(10));
		productRequestBody.setDescription(RandomGenerator.generateString(50));
		productRequestBody.setPrice(RandomGenerator.randomPositiveDouble(100));

		return productRequestBody;
	}

}
