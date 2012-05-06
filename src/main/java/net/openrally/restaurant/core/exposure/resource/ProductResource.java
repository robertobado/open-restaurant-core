package net.openrally.restaurant.core.exposure.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.exception.ConflictException;
import net.openrally.restaurant.core.exception.ForbiddenException;
import net.openrally.restaurant.core.exception.NotFoundException;
import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.persistence.dao.ProductDAO;
import net.openrally.restaurant.core.persistence.entity.Product;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.ProductRequestBody;
import net.openrally.restaurant.core.response.body.ProductListResponseBody;
import net.openrally.restaurant.core.response.body.ProductResponseBody;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("/product")
@Component
@Transactional
public class ProductResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ProductDAO productDAO;

	public static final String PATH = "product";

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response post(String requestBody,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, ForbiddenException, URISyntaxException,
			UnauthorizedException {

		User user = getRequestUser(loginToken);

		ProductRequestBody productRequestBody = retrieveProductRequestBody(requestBody);

		logger.debug("Creating new product");

		Product product = new Product();

		product.setCompany(user.getCompany());
		product.setDescription(productRequestBody.getDescription());
		product.setName(productRequestBody.getName());

		logger.debug("Saving new product");

		try {
			productDAO.save(product);
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ product.getProductId());

		logger.debug("Finished processing request successfully");

		return Response.created(locationURI).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response getList(
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		User user = getRequestUser(loginToken);

		List<Product> entityList = productDAO.getAllByCompanyId(user
				.getCompany().getCompanyId());

		List<ProductResponseBody> entityResponseBodyList = new LinkedList<ProductResponseBody>();

		for (Product entity : entityList) {
			ProductResponseBody entityResponseBody = new ProductResponseBody(
					entity);
			entityResponseBodyList.add(entityResponseBody);
		}

		ProductListResponseBody entityListResponseBody = new ProductListResponseBody();

		entityListResponseBody.setList(entityResponseBodyList);

		return Response.ok(gson.toJson(entityListResponseBody)).build();
	}

	@GET
	@Path("/{entityId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response get(@PathParam("entityId") String entityIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		User user = getRequestUser(loginToken);

		Product product = retrieveProduct(entityIdString);

		if (Long.compare(product.getCompany().getCompanyId(), user.getCompany()
				.getCompanyId()) != 0) {
			throw new ForbiddenException();
		}

		ProductResponseBody productResponseBody = new ProductResponseBody(
				product);

		return Response.ok(gson.toJson(productResponseBody)).build();
	}

	@DELETE
	@Path("/{entityId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = ConflictException.class)
	public Response delete(@PathParam("entityId") String entityIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			ConflictException, UnauthorizedException {

		User user = getRequestUser(loginToken);

		Product product = retrieveProduct(entityIdString);

		if (Long.compare(product.getCompany().getCompanyId(), user.getCompany()
				.getCompanyId()) != 0) {
			throw new ForbiddenException();
		}

		try {
			productDAO.delete(product);
			productDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new ConflictException(
					MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES);
		}

		return Response.noContent().build();
	}

	@PUT
	@Path("/{entityId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response put(String requestBody,
			@PathParam("entityId") String entityIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			URISyntaxException, UnauthorizedException {

		User user = getRequestUser(loginToken);

		Product product = retrieveProduct(entityIdString);

		if (Long.compare(product.getCompany().getCompanyId(), user.getCompany()
				.getCompanyId()) != 0) {
			throw new ForbiddenException();
		}

		ProductRequestBody productRequestBody = retrieveProductRequestBody(requestBody);

		product.setName(productRequestBody.getName());
		product.setDescription(productRequestBody.getDescription());

		try {
			productDAO.update(product);
			productDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(
					MSG_ENTITY_IS_ASSOCIATED_WITH_OTHER_ENTITIES_OR_DUPLICATE);
		}

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ product.getProductId());

		logger.debug("Finished processing request successfully");

		return Response.ok().contentLocation(locationURI).build();
	}

	private Product retrieveProduct(String productIdString)
			throws BadRequestException, NotFoundException {
		Long productId;

		logger.debug("Retrieving product from id parameter");
		logger.debug("Converting product id from string to Long");

		try {
			productId = Long.parseLong(productIdString);
		} catch (NumberFormatException e) {
			logger.debug("Malformed product id: " + productIdString);
			throw new BadRequestException(MSG_INVALID_ENTITY_IDENTIFIER);
		}

		Product product = productDAO.get(productId);

		if (null == product) {
			throw new NotFoundException();
		}

		return product;
	}

	private ProductRequestBody retrieveProductRequestBody(
			String requestBodyString) throws BadRequestException {
		ProductRequestBody productRequestBody;

		try {
			productRequestBody = gson.fromJson(requestBodyString,
					ProductRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == productRequestBody) {
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}

		productRequestBody.validate();

		return productRequestBody;
	}

}
