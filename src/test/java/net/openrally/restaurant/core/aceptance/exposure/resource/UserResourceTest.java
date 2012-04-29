package net.openrally.restaurant.core.aceptance.exposure.resource;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class UserResourceTest extends BaseResourceTest{

	private static final String PATH = "user";
	
	public UserResourceTest() throws Exception {
		super();
	}
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		startServer();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		stopServer();
	}
	
	@Test
	public void testWrongContentTypePost() throws ClientProtocolException,
			IOException {
		testWrongContentTypePost(PATH);
	}

	@Test
	public void testWrongAcceptTypePost() throws ClientProtocolException,
			IOException {
		testWrongAcceptTypePost(PATH);
	}
	
	@Test
	public void testInvalidJsonPost() throws ClientProtocolException, IOException {
		testInvalidJsonPost(PATH);
	}

	@Test
	public void testEmptyRequestBodyPost() throws ClientProtocolException,
			IOException {
		testEmptyRequestBodyPost(PATH);
	}
	
	@Test
	public void testWrongContentTypeGet() throws ClientProtocolException, IOException{
//		testWrongContentTypeGet(PATH + SLASH + 123);
	}
	
	@Test
	public void testPostEmptyUser(){
		Assert.fail(); 
	}
	
	@Test
	public void testPostDuplicateUser(){
		Assert.fail(); 
	}
	
	@Test
	public void testPostCorrectUser(){
		Assert.fail(); 
	}
	
	@Test
	public void testPutEmptyUser(){
		Assert.fail(); 
	}
	
	@Test
	public void testPutNonExistingUser(){
		Assert.fail(); 
	}
	
	@Test
	public void testPutDuplicateUser(){
		Assert.fail(); 
	}
	
	@Test
	public void testPutCorrectUser(){
		Assert.fail(); 
	}
	
	@Test
	public void testDeleteNonExistingUser(){
		Assert.fail(); 
	}
	
	@Test
	public void testDeleteUserTwice(){
		Assert.fail(); 
	}
	
	@Test
	public void testDeleteCorrectUser(){
		Assert.fail(); 
	}
	
//	@Test
//	public void testGetNonExistingUser() throws ClientProtocolException, IOException{
//		HttpGet httpGet = generateBasicHttpGet(PATH + SLASH + 999);
//		
//		httpGet.addHeader(LOGIN_TOKEN_HEADER_PARAMETER_NAME, value);
//		
//		HttpResponse response = getHttpClient().execute(httpGet);
//
//		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response
//				.getStatusLine().getStatusCode());
//	}
	
	@Test
	public void testGetDeletedUser(){
		Assert.fail(); 
	}
	
	@Test
	public void testGetCorrectUser(){
		Assert.fail(); 
	}
	

}
