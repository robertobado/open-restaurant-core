package net.openrally.restaurant.core.security;

import net.openrally.restaurant.core.aceptance.exposure.resource.BaseResourceTest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class AuthorizationFilterTest extends BaseResourceTest{

	protected AuthorizationFilterTest() throws Exception {
		super();
	}

}
