package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;
import net.openrally.restaurant.core.exposure.resource.BaseResource;
import net.openrally.restaurant.core.persistence.dao.CompanyDAO;
import net.openrally.restaurant.core.persistence.dao.ConfigurationDAO;
import net.openrally.restaurant.core.persistence.dao.LoginTokenDAO;
import net.openrally.restaurant.core.persistence.dao.PermissionDAO;
import net.openrally.restaurant.core.persistence.dao.RoleDAO;
import net.openrally.restaurant.core.persistence.dao.UserDAO;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;
import net.openrally.restaurant.core.persistence.entity.LoginToken;
import net.openrally.restaurant.core.persistence.entity.Permission;
import net.openrally.restaurant.core.persistence.entity.Role;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.util.RandomGenerator;
import net.openrally.restaurant.core.utils.HashCalculator;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.hisrc.hifaces20.testing.server.WebServerEnvironment;
import org.hisrc.hifaces20.testing.server.jetty.JettyWebServerEnvironment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.google.gson.Gson;
import com.sun.jersey.spi.container.ContainerRequest;

@ContextConfiguration("/applicationContext.xml")
public class BaseResourceTest {

	protected Gson gson = new Gson();

	private DefaultHttpClient httpclient;

	@Autowired
	protected UserDAO userDAO;

	@Autowired
	protected ConfigurationDAO configurationDAO;

	@Autowired
	protected CompanyDAO companyDAO;

	@Autowired
	protected LoginTokenDAO loginTokenDAO;

	@Autowired
	protected RoleDAO roleDAO;

	@Autowired
	protected PermissionDAO permissionDAO;

	protected String authorizedToken;

	private static WebServerEnvironment webServerEnvironment;

	private static WebServerEnvironment createWebServerEnvironment() {
		return new JettyWebServerEnvironment(BaseResource.SERVER_HOSTNAME,
				BaseResource.PORT_NUMBER, BaseResource.CONTEXT_PATH,
				"src/main/webapp");
	}

	protected static synchronized void startServer() throws Exception {
		if (null == webServerEnvironment) {
			webServerEnvironment = createWebServerEnvironment();
		}

		webServerEnvironment.start();
	}

	protected static synchronized void stopServer() throws Exception {
		if (null != webServerEnvironment) {
			webServerEnvironment.stop();
		}
	}

	protected BaseResourceTest() throws Exception {
		httpclient = new DefaultHttpClient();
	}

	protected final Gson getGsonInstance() {
		return gson;
	}

	protected final String getServerBasePath() {
		return BaseResource.getServerBasePath();
	}

	protected HttpClient getHttpClient() {
		return httpclient;
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		startServer();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		stopServer();
	}

	protected void testWrongContentTypePost(String path)
			throws ClientProtocolException, IOException {

		HttpPost httpPost = new HttpPost(getServerBasePath()
				+ BaseResource.SLASH + path);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_XML);
		httpPost.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpPost.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(),
				response.getStatusLine().getStatusCode());
	}

	protected void testWrongAcceptTypePost(String path)
			throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(getServerBasePath()
				+ BaseResource.SLASH + path);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_XML);
		httpPost.addHeader(acceptHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpPost.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	protected void testInvalidJsonPost(String path)
			throws ClientProtocolException, IOException {

		HttpPost httpPost = generateBasicHttpPost(path);

		httpPost.setEntity(new StringEntity("Invalid Json"));

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	protected void testEmptyRequestBodyPost(String path)
			throws ClientProtocolException, IOException {
		HttpPost httpPost = generateBasicHttpPost(path);

		HttpResponse response = getHttpClient().execute(httpPost);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	protected HttpPost generateBasicHttpPost(String path) {
		HttpPost httpPost = new HttpPost(getServerBasePath()
				+ BaseResource.SLASH + path);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_JSON);
		httpPost.addHeader(acceptHeader);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON);
		httpPost.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpPost.addHeader(loginTokenHeader);
		}

		return httpPost;
	}

	protected void testWrongContentTypeGet(String path)
			throws ClientProtocolException, IOException {

		HttpGet httpGet = new HttpGet(getServerBasePath() + BaseResource.SLASH
				+ path + BaseResource.SLASH + 1);

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

	protected void testWrongAcceptTypeGet(String path)
			throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(getServerBasePath() + BaseResource.SLASH
				+ path + BaseResource.SLASH + 1);

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

	protected HttpGet generateBasicHttpGet(String path) {
		HttpGet httpGet = new HttpGet(getServerBasePath() + BaseResource.SLASH
				+ path);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_JSON);
		httpGet.addHeader(acceptHeader);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON);
		httpGet.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpGet.addHeader(loginTokenHeader);
		}

		return httpGet;
	}

	protected void testWrongContentTypeDelete(String path)
			throws ClientProtocolException, IOException {

		HttpDelete httpDelete = new HttpDelete(getServerBasePath()
				+ BaseResource.SLASH + path + BaseResource.SLASH + 1);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_XML);
		httpDelete.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpDelete.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(),
				response.getStatusLine().getStatusCode());
	}

	protected void testWrongAcceptTypeDelete(String path)
			throws ClientProtocolException, IOException {
		HttpDelete httpDelete = new HttpDelete(getServerBasePath()
				+ BaseResource.SLASH + path + BaseResource.SLASH + 1);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_XML);
		httpDelete.addHeader(acceptHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpDelete.addHeader(loginTokenHeader);
		}

		HttpResponse response = getHttpClient().execute(httpDelete);

		Assert.assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	protected HttpDelete generateBasicHttpDelete(String path) {
		HttpDelete httpDelete = new HttpDelete(getServerBasePath()
				+ BaseResource.SLASH + path);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_JSON);
		httpDelete.addHeader(acceptHeader);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON);
		httpDelete.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpDelete.addHeader(loginTokenHeader);
		}

		return httpDelete;
	}

	protected void testWrongContentTypePut(String path)
			throws ClientProtocolException, IOException {

		HttpPut httpPut = new HttpPut(getServerBasePath() + BaseResource.SLASH
				+ path + BaseResource.SLASH + 1);

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

	protected void testWrongAcceptTypePut(String path)
			throws ClientProtocolException, IOException {
		HttpPut httpPut = new HttpPut(getServerBasePath() + BaseResource.SLASH
				+ path + BaseResource.SLASH + 1);

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

	protected void testInvalidJsonPut(String path)
			throws ClientProtocolException, IOException {

		HttpPut httpPut = generateBasicHttpPut(path);

		httpPut.setEntity(new StringEntity("Invalid Json"));

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	protected void testEmptyRequestBodyPut(String path)
			throws ClientProtocolException, IOException {
		HttpPut httpPut = generateBasicHttpPut(path);

		HttpResponse response = getHttpClient().execute(httpPut);

		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response
				.getStatusLine().getStatusCode());
	}

	protected HttpPut generateBasicHttpPut(String path) {
		HttpPut httpPut = new HttpPut(getServerBasePath() + BaseResource.SLASH
				+ path);

		Header acceptHeader = new BasicHeader(HttpHeaders.ACCEPT,
				MediaType.APPLICATION_JSON);
		httpPut.addHeader(acceptHeader);

		Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON);
		httpPut.addHeader(contentTypeHeader);

		if (!StringUtils.isBlank(authorizedToken)) {
			Header loginTokenHeader = new BasicHeader(
					ContainerRequest.AUTHORIZATION, authorizedToken);
			httpPut.addHeader(loginTokenHeader);
		}

		return httpPut;
	}

	protected Company createCompanyAndPersist() {
		Company company = new Company();
		companyDAO.save(company);
		return company;
	}

	protected Configuration createConfigurationAndPersist(Company company) {
		Configuration configuration = new Configuration();

		configuration.setCompany(company);
		configuration.setHashSalt(RandomGenerator.generateString());
		configuration.setLoginTokenLifeTime(100L);

		configurationDAO.save(configuration);

		return configuration;
	}

	protected User createUserAndPersist(Configuration configuration) {
		String randomPassword = RandomGenerator.generateString();
		return createUserAndPersist(configuration, randomPassword);
	}

	protected User createUserAndPersist(Configuration configuration,
			String password) {
		User user = new User();

		user.setCompany(configuration.getCompany());
		user.setLogin(RandomGenerator.generateString());

		String passwordWithSalt = configuration.getHashSalt() + "/" + password;
		String passwordHash = HashCalculator.generateMD5Hash(passwordWithSalt);

		user.setPasswordHash(passwordHash);

		userDAO.save(user);

		return user;
	}

	protected LoginToken createLoginTokenAndPersist(User user) {
		LoginToken loginToken = new LoginToken();

		loginToken
				.setExpirationTime((System.currentTimeMillis() / 1000) + 1000);
		loginToken.setToken(RandomGenerator.generateString(10));
		loginToken.setUser(user);

		loginTokenDAO.save(loginToken);

		return loginToken;
	}

	protected Role createRoleAndPersist(Company company) {
		Role role = new Role();

		role.setCompany(company);
		role.setDescription(RandomGenerator.generateString(50));
		role.setName(RandomGenerator.generateString(10));
		roleDAO.save(role);

		return role;
	}

	protected void associateRoleWithUser(Role role, User user) {
		Set<Role> roleSet = new HashSet<Role>();
		roleSet.add(role);

		user.setRoles(roleSet);
		userDAO.update(user);
	}

	protected Permission createFullPermissionAndPersist(Role role) {
		Permission permission = new Permission();

		permission.setRole(role);
		permission.setPath("/");
		permission.setAllowPost(true);
		permission.setAllowPut(true);
		permission.setAllowGet(true);
		permission.setAllowDelete(true);

		permissionDAO.save(permission);

		return permission;
	}

	protected Permission createRandomPermissionAndPersist(Role role){
		Permission permission = new Permission();
		
		permission.setRole(role);
		permission.setPath("/" + RandomGenerator.generateString(10));
		permission.setAllowPost(RandomGenerator.randomBoolean());
		permission.setAllowPut(RandomGenerator.randomBoolean());
		permission.setAllowGet(RandomGenerator.randomBoolean());
		permission.setAllowDelete(RandomGenerator.randomBoolean());
		
		permissionDAO.save(permission);
		
		return permission;
	}
}
