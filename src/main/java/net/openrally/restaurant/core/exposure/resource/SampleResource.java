package net.openrally.restaurant.core.exposure.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import net.openrally.restaurant.core.persistence.dao.CityDAO;
import net.openrally.restaurant.core.persistence.entity.City;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.spi.resource.Singleton;

@Path("/sample")
@Component
@Transactional
@Singleton
public class SampleResource {
	private String sampleText = "Default sample text!";
	private final Log log = LogFactory.getLog(SampleResource.class);
	
	private CityDAO cityDAO;
	
	@Autowired
	public void setCityDAO(CityDAO cityDAO){
		this.cityDAO = cityDAO;
	}

	@SuppressWarnings("unchecked")
	@GET
	@Produces("text/plain")
	public String getSampleText() throws IOException {

		try {
			
			City city1 = new City();
			city1.setName("New York");
			cityDAO.save(city1);

			City city2 = new City();
			city2.setName("Rio de Janeiro");
			cityDAO.save(city2);

			City city3 = new City();
			city3.setName("Tokyo");
			cityDAO.save(city3);

			City city4 = new City();
			city4.setName("London");
			cityDAO.save(city4);

			cityDAO.getList();

			city4.setName("Paris");

			cityDAO.update(city4);

			cityDAO.delete(city3);

			List<City> listaCidades = cityDAO.getList();

			log.trace("In get sample text");

		} catch (Exception e) {
			System.out.println("BADO=Exception has happened: " + e);
			e.printStackTrace();
		}

		return sampleText;
	}

	@POST
	@Consumes("text/plain")
	@Produces("text/plain")
	public Response setSampleText(String incomingText) {
		log.trace("In set sample text");
		this.sampleText = incomingText;
		return Response.ok().build();
	}
}
