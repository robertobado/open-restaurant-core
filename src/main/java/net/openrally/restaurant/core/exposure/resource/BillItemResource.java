package net.openrally.restaurant.core.exposure.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.exception.ConflictException;
import net.openrally.restaurant.core.exception.ForbiddenException;
import net.openrally.restaurant.core.exception.NotFoundException;
import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.persistence.dao.BillDAO;
import net.openrally.restaurant.core.persistence.dao.BillItemDAO;
import net.openrally.restaurant.core.persistence.dao.ProductDAO;
import net.openrally.restaurant.core.persistence.entity.Bill;
import net.openrally.restaurant.core.persistence.entity.BillItem;
import net.openrally.restaurant.core.persistence.entity.Product;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.BillItemRequestBody;
import net.openrally.restaurant.core.response.body.BillItemListResponseBody;
import net.openrally.restaurant.core.response.body.BillItemResponseBody;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("/bill-item")
@Component
@Transactional
public class BillItemResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String PATH = "bill-item";
	
	@Autowired
	private BillItemDAO billItemDAO;
	
	@Autowired
	private BillDAO billDAO;
	
	@Autowired
	private ProductDAO productDAO;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response post(String requestBody,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, ForbiddenException, URISyntaxException,
			UnauthorizedException, ConflictException {

		User user = getRequestUser(loginToken);

		BillItemRequestBody entityRequestBody = retrieveEntityRequestBody(requestBody);

		Bill bill = billDAO
				.get(entityRequestBody.getBillId());

		if (Long.compare(bill.getConsumptionIdentifier().getCompany().getCompanyId(),
				user.getCompany().getCompanyId()) != 0) {
			throw new ForbiddenException();
		}
		
		if(!StringUtils.equals(bill.getStatus(), BillResource.Status.OPEN.toString())){
			throw new ConflictException("Parent bill not open");
		}
		
		Product product = productDAO
				.get(entityRequestBody.getProductId());

		if (Long.compare(bill.getConsumptionIdentifier().getCompany().getCompanyId(),
				user.getCompany().getCompanyId()) != 0) {
			throw new ForbiddenException();
		}

		logger.debug("Creating new bill item");

		BillItem billItem = new BillItem();
		
		billItem.setBill(bill);
		billItem.setProduct(product);
		billItem.setQuantity(entityRequestBody.getQuantity());
		billItem.setUnitPrice(product.getPrice());

		logger.debug("Saving new bill item");

		try {
			billItemDAO.save(billItem);
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ billItem.getBillItemId());

		logger.debug("Finished processing request successfully");

		return Response.created(locationURI).build();
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

		BillItem billItem = retrieveEntity(entityIdString);

		if (Long.compare(billItem.getBill().getConsumptionIdentifier().getCompany()
				.getCompanyId(), user.getCompany().getCompanyId()) != 0) {
			throw new ForbiddenException();
		}

		BillItemResponseBody entityResponseBody = new BillItemResponseBody(billItem);

		return Response.ok(gson.toJson(entityResponseBody)).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response getList(
			@QueryParam("billId") String billIdString,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		User user = getRequestUser(loginToken);

		if (StringUtils.isBlank(billIdString)) {
			throw new BadRequestException("Missing billId parameter value");
		}

		Long billId = tryLongFilterCast(billIdString);

		Bill bill = billDAO
				.get(billId);

		if (null == bill) {
			throw new NotFoundException("Invalid billId");
		}

		if (Long.compare(bill.getConsumptionIdentifier().getCompany().getCompanyId(),
				user.getCompany().getCompanyId()) != 0) {
			throw new ForbiddenException();
		}
		
		try{

		List<BillItem> entityList = billItemDAO.getAllByCompanyIdAndBillId(user
				.getCompany().getCompanyId(), bill.getBillId());
		
		

		List<BillItemResponseBody> entityResponseBodyList = new LinkedList<BillItemResponseBody>();

		for (BillItem entity : entityList) {
			BillItemResponseBody entityResponseBody = new BillItemResponseBody(entity);
			entityResponseBodyList.add(entityResponseBody);
		}

		BillItemListResponseBody entityListResponseBody = new BillItemListResponseBody();

		entityListResponseBody.setList(entityResponseBodyList);

		return Response.ok(gson.toJson(entityListResponseBody)).build();
		
		}
		catch(Throwable t){
			return null;
		}
	}
	
	private BillItemRequestBody retrieveEntityRequestBody(String requestBodyString)
			throws BadRequestException {
		BillItemRequestBody entityRequestBody;

		try {
			entityRequestBody = gson.fromJson(requestBodyString,
					BillItemRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == entityRequestBody) {
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}

		entityRequestBody.validate();

		return entityRequestBody;
	}
	
	private BillItem retrieveEntity(String entityIdString)
			throws BadRequestException, NotFoundException {
		Long entityId;

		logger.debug("Retrieving bill item from id parameter");
		logger.debug("Converting bill item id from string to Long");

		try {
			entityId = Long.parseLong(entityIdString);
		} catch (NumberFormatException e) {
			logger.debug("Malformed bill item id: " + entityIdString);
			throw new BadRequestException(MSG_INVALID_ENTITY_IDENTIFIER);
		}

		BillItem billItem = billItemDAO.get(entityId);

		if (null == billItem) {
			throw new NotFoundException();
		}

		return billItem;
	}
	
	private Long tryLongFilterCast(String filterValueString)
			throws BadRequestException {
		try {
			return Long.parseLong(filterValueString);
		} catch (NumberFormatException e) {
			logger.debug("Malformed Long filter parameter value: "
					+ filterValueString);
			throw new BadRequestException("Invalid filter value");
		}
	}

}
