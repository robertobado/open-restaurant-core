package net.openrally.restaurant.core.exposure.resource;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( "/applicationContext.xml")
public class SampleResourceTest extends JerseyTest {
	private WebResource webResource;

	public SampleResourceTest() throws Exception {
		super("net.openrally.restaurant.core.exposure.resource");
	}

	@Before
	public void before() {
		this.webResource = resource();

	}

	@Test
	public void getSampleResource() throws IOException {
		String responseMsg = webResource.path("sample").get(String.class);
		assertTrue(responseMsg.equals("Default sample text!"));
	}

	@Test
	public void postSampleResource() throws IOException, URISyntaxException {
		final String testText = FileUtils.readFileToString(new File(getClass()
				.getResource("/postSampleResource.txt").toURI()));
		final String postResult = webResource.path("sample").post(String.class,
				testText);
		assertTrue(StringUtils.isEmpty(postResult));

		final String getResult = webResource.path("sample").get(String.class);
		assertTrue(getResult.equals(testText));
	}
}
