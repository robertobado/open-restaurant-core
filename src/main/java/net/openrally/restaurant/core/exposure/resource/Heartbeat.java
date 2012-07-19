package net.openrally.restaurant.core.exposure.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Path("/heartbeat")
@Component
@Transactional
public class Heartbeat extends BaseResource {
	
	@GET
	@Transactional(readOnly = true)
	public Response get(){

		return Response.ok("All checks ok").build();
	}
}
