package net.openrally.restaurant.core.exposure.resource;

import com.google.gson.Gson;
import com.sun.jersey.test.framework.JerseyTest;

public class BaseResourceTest extends JerseyTest {
	protected static final String CONTENT_TYPE = "content-type";
	protected Gson gson = new Gson();
	
	protected BaseResourceTest(String packageName){
		super(packageName);
	}
}
