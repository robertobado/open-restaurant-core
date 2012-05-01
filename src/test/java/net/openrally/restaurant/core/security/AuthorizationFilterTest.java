package net.openrally.restaurant.core.security;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.aceptance.exposure.resource.BaseResourceTest;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.exposure.resource.UserResource;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.RandomGenerator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.spi.container.ContainerRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class AuthorizationFilterTest extends BaseResourceTest {

	private Company company;
	private Configuration configuration;
	private User user;
	private LoginToken loginToken;
	private Role role;

	public AuthorizationFilterTest() throws Exception {
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
	}

	@After
	public void tearDownEntities() {
		user.setRoles(null);
		userDAO.update(user);
		roleDAO.delete(role);
		loginTokenDAO.delete(loginToken);
		userDAO.delete(user);
		configurationDAO.delete(configuration);
		companyDAO.delete(company);
	}

	@Test
	public void testNoToken() throws ClientProtocolException, IOException {
		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		httpGet.removeHeaders(ContainerRequest.AUTHORIZATION);

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testInvalidToken() throws ClientProtocolException, IOException {
		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		httpGet.removeHeaders(ContainerRequest.AUTHORIZATION);
		httpGet.addHeader(ContainerRequest.AUTHORIZATION, "invalid-token");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testExpiredToken() throws ClientProtocolException, IOException {

		loginToken.setExpirationTime((System.currentTimeMillis() / 1000) - 10);
		loginTokenDAO.update(loginToken);

		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	@Test
	public void testForbiddenPost() throws ClientProtocolException, IOException {
		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath(BaseResource.SLASH + UserResource.PATH);
		permission.setAllowPost(false);
		permission.setAllowPut(true);
		permission.setAllowGet(true);
		permission.setAllowDelete(true);

		permissionDAO.save(permission);

		HttpPost httpPost = generateBasicHttpPost(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		permissionDAO.delete(permission);
	}

	@Test
	public void testAllowedPost() throws ClientProtocolException, IOException {
		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath(BaseResource.SLASH + UserResource.PATH);
		permission.setAllowPost(true);
		permission.setAllowPut(false);
		permission.setAllowGet(false);
		permission.setAllowDelete(false);

		permissionDAO.save(permission);

		HttpPost httpPost = generateBasicHttpPost(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertNotSame(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		permissionDAO.delete(permission);
	}

	@Test
	public void testForbiddenPut() throws ClientProtocolException, IOException {
		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath(BaseResource.SLASH + UserResource.PATH);
		permission.setAllowPost(true);
		permission.setAllowPut(false);
		permission.setAllowGet(true);
		permission.setAllowDelete(true);

		permissionDAO.save(permission);

		HttpPut httpPut = generateBasicHttpPut(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		permissionDAO.delete(permission);
	}

	@Test
	public void testAllowedPut() throws ClientProtocolException, IOException {
		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath(BaseResource.SLASH + UserResource.PATH);
		permission.setAllowPost(false);
		permission.setAllowPut(true);
		permission.setAllowGet(false);
		permission.setAllowDelete(false);

		permissionDAO.save(permission);

		HttpPut httpPut = generateBasicHttpPut(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertNotSame(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		permissionDAO.delete(permission);
	}

	@Test
	public void testForbiddenGet() throws ClientProtocolException, IOException {
		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath(BaseResource.SLASH + UserResource.PATH);
		permission.setAllowPost(true);
		permission.setAllowPut(true);
		permission.setAllowGet(false);
		permission.setAllowDelete(true);

		permissionDAO.save(permission);

		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		permissionDAO.delete(permission);
	}

	@Test
	public void testAllowedGet() throws ClientProtocolException, IOException {
		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath(BaseResource.SLASH + UserResource.PATH);
		permission.setAllowPost(false);
		permission.setAllowPut(false);
		permission.setAllowGet(true);
		permission.setAllowDelete(false);

		permissionDAO.save(permission);

		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertNotSame(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		permissionDAO.delete(permission);
	}

	@Test
	public void testParametersRemoval() throws ClientProtocolException, IOException {
		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath(BaseResource.SLASH + UserResource.PATH);
		permission.setAllowPost(false);
		permission.setAllowPut(false);
		permission.setAllowGet(true);
		permission.setAllowDelete(false);

		permissionDAO.save(permission);

		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3) + "?foo=bar&alfa=beta&gama=delta&xpto=ipsilanty");

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertNotSame(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		permissionDAO.delete(permission);
	}

	@Test
	public void testRootAccess() throws ClientProtocolException, IOException {
		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath(BaseResource.SLASH);
		permission.setAllowPost(false);
		permission.setAllowPut(false);
		permission.setAllowGet(true);
		permission.setAllowDelete(false);

		permissionDAO.save(permission);

		HttpGet httpGet = generateBasicHttpGet(UserResource.PATH
				+ BaseResource.SLASH + RandomGenerator.randomPositiveInt(3));

		HttpResponse response = getHttpClient().execute(httpGet);

		Assert.assertNotSame(Status.FORBIDDEN.getStatusCode(), response
				.getStatusLine().getStatusCode());

		permissionDAO.delete(permission);
	}

}
