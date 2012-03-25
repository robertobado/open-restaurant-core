package net.openrally.restaurant.core.exposure.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;

@Component
@Transactional
@Singleton
@Produces("application/json")
@Consumes("application/json")
public abstract class BaseResource {
	protected Gson gson = new Gson();
}
