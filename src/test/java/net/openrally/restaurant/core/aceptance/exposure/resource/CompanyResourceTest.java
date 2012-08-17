package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.CompanyResource;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.core.util.StringUtilities;
import net.openrally.restaurant.request.body.CompanyRequestBody;
import net.openrally.restaurant.response.body.CompanyResponseBody;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class CompanyResourceTest extends BaseResourceTest {

	public CompanyResourceTest() throws Exception {
		super();
	}

	@Test
	public void testWrongContentTypePost() throws ClientProtocolException,
			IOException {
		testWrongContentTypePost(CompanyResource.PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(CompanyResource.PATH);
	}

	@Test
	public void testPostCorrectEntity() throws ClientProtocolException,
			IOException {

		HttpPost httpPost = generateBasicHttpPost(CompanyResource.PATH);

		CompanyRequestBody entityRequestBody = generateBasicEntityRequestBody();

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

		String responseBody = StringUtilities.httpResponseAsString(response);

		CompanyResponseBody entityResponseBody = gson.fromJson(responseBody,
				CompanyResponseBody.class);

		Assert.assertNotNull(entityResponseBody.getCompanyId());
		Assert.assertTrue(entityResponseBody.getCompanyId() > 0);
		Assert.assertFalse(StringUtils.isBlank(entityResponseBody.getUsername()));
		Assert.assertFalse(StringUtils.isBlank(entityResponseBody.getPassword()));
		Assert.assertEquals(entityRequestBody.getCompanyName(), entityResponseBody.getCompanyName());
		
		Configuration configuration = configurationDAO
				.loadByCompanyId(entityResponseBody.getCompanyId());
		configurationDAO.delete(configuration);

		List<User> userList = userDAO.listAllByCompanyId(entityResponseBody
				.getCompanyId());
		for (User user : userList) {
			userDAO.delete(user);
		}

		List<Role> roleList = roleDAO.listAllByCompanyId(entityResponseBody
				.getCompanyId());
		for (Role role : roleList) {

			List<Permission> permissionList = permissionDAO.listByRoleId(role
					.getRoleId());
			for (Permission permission : permissionList) {
				permissionDAO.delete(permission);
			}

			roleDAO.delete(role);
		}

		Company company = companyDAO.get(entityResponseBody.getCompanyId());
		companyDAO.delete(company);

	}

	// Utilitary functions
	private CompanyRequestBody generateBasicEntityRequestBody() {
		CompanyRequestBody entityRequestBody = new CompanyRequestBody();

		entityRequestBody.setCompanyName(RandomGenerator.generateString(20));
		
		return entityRequestBody;
	}
}
