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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.openrally.restaurant.core.exception.BadRequestException;
import net.openrally.restaurant.core.exception.ConflictException;
import net.openrally.restaurant.core.exception.ForbiddenException;
import net.openrally.restaurant.core.exception.NotFoundException;
import net.openrally.restaurant.core.exception.UnauthorizedException;
import net.openrally.restaurant.core.persistence.dao.BillDAO;
import net.openrally.restaurant.core.persistence.dao.ConsumptionIdentifierDAO;
import net.openrally.restaurant.core.persistence.entity.Bill;
import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;
import net.openrally.restaurant.core.persistence.entity.User;
import net.openrally.restaurant.core.request.body.BillRequestBody;
import net.openrally.restaurant.core.response.body.BillListResponseBody;
import net.openrally.restaurant.core.response.body.BillResponseBody;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonSyntaxException;
import com.sun.jersey.spi.container.ContainerRequest;

@Path("/bill")
@Component
@Transactional
public class BillResource extends BaseResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String PATH = "bill";

	public static enum Status {
		OPEN("open"), CLOSED("closed");

		private String value;

		private Status(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	@Autowired
	private BillDAO billDAO;

	@Autowired
	private ConsumptionIdentifierDAO consumptionIdentifierDAO;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = BadRequestException.class)
	public Response post(String requestBody,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, ForbiddenException, URISyntaxException,
			UnauthorizedException, ConflictException {

		User user = getRequestUser(loginToken);

		BillRequestBody entityRequestBody = retrieveEntityRequestBody(requestBody);

		ConsumptionIdentifier consumptionIdentifier = consumptionIdentifierDAO
				.get(entityRequestBody.getConsumptionIdentifierId());

		if (Long.compare(consumptionIdentifier.getCompany().getCompanyId(),
				user.getCompany().getCompanyId()) != 0) {
			throw new ForbiddenException();
		}

		if (StringUtils.equals(entityRequestBody.getStatus(),
						Status.OPEN.toString())) {
			logger.debug("Checking if there is any open bill for same consumption identifier");
			
			List<Bill> existingBillList = billDAO
					.listBillsByConsumptionIdentifierIdAndStatus(
							consumptionIdentifier.getConsumptionIdentifierId(),
							Status.OPEN.toString());

			if (existingBillList.size() > 0) {
				throw new ConflictException(
						"Consumption identifier already has a open bill");
			}
		}

		logger.debug("Validating status parameter");

		validateStatusValue(entityRequestBody.getStatus());

		logger.debug("Creating new bill");

		Bill bill = new Bill();

		bill.setStatus(entityRequestBody.getStatus());
		bill.setConsumptionIdentifier(consumptionIdentifier);
		bill.setOpenTimestamp(entityRequestBody.getOpenTimestamp());
		bill.setCloseTimestamp(entityRequestBody.getCloseTimestamp());

		if (null == bill.getOpenTimestamp()) {
			bill.setOpenTimestamp(System.currentTimeMillis() / 1000);
		}

		if (StringUtils.equals(bill.getStatus(), Status.CLOSED.toString())
				&& null == bill.getCloseTimestamp()) {
			bill.setCloseTimestamp(System.currentTimeMillis() / 1000);
		}

		logger.debug("Saving new bill");

		try {
			billDAO.save(bill);
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ bill.getBillId());

		logger.debug("Finished processing request successfully");

		return Response.created(locationURI).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true)
	public Response getList(
			@QueryParam("consumptionIdentifierId") String consumptionIdentifierIdString,
			@QueryParam("startOpenTimestamp") String startOpenTimestampString,
			@QueryParam("endOpenTimestamp") String endOpenTimestampString,
			@QueryParam("startCloseTimestamp") String startCloseTimestampString,
			@QueryParam("endCloseTimestamp") String endCloseTimestampString,
			@QueryParam("status") List<String> statusList,
			@HeaderParam(ContainerRequest.AUTHORIZATION) String loginToken)
			throws BadRequestException, NotFoundException, ForbiddenException,
			UnauthorizedException {

		User user = getRequestUser(loginToken);

		Long consumptionIdentifierId = null;

		if (!StringUtils.isBlank(consumptionIdentifierIdString)) {
			consumptionIdentifierId = tryLongFilterCast(consumptionIdentifierIdString);

			ConsumptionIdentifier consumptionIdentifier = consumptionIdentifierDAO
					.get(consumptionIdentifierId);

			if (null == consumptionIdentifier) {
				throw new NotFoundException("Invalid consumptionIdentifierId");
			}

			if (Long.compare(consumptionIdentifier.getCompany().getCompanyId(),
					user.getCompany().getCompanyId()) != 0) {
				throw new ForbiddenException();
			}
		}

		if (statusList != null && statusList.size() > 0) {
			for (String status : statusList) {
				validateStatusValue(status);
			}
		}

		Long startOpenTimestamp = null;

		if (!StringUtils.isBlank(startOpenTimestampString)) {
			startOpenTimestamp = tryLongFilterCast(startOpenTimestampString);
		}

		Long endOpenTimestamp = null;

		if (!StringUtils.isBlank(endOpenTimestampString)) {
			endOpenTimestamp = tryLongFilterCast(endOpenTimestampString);
		}

		Long startCloseTimestamp = null;

		if (!StringUtils.isBlank(startCloseTimestampString)) {
			startCloseTimestamp = tryLongFilterCast(startCloseTimestampString);
		}

		Long endCloseTimestamp = null;

		if (!StringUtils.isBlank(endCloseTimestampString)) {
			endCloseTimestamp = tryLongFilterCast(endCloseTimestampString);
		}

		List<Bill> entityList = billDAO.getAllByCompanyIdWithFilters(user
				.getCompany().getCompanyId(), consumptionIdentifierId,
				startOpenTimestamp, endOpenTimestamp, startCloseTimestamp,
				endCloseTimestamp, statusList);

		List<BillResponseBody> entityResponseBodyList = new LinkedList<BillResponseBody>();

		for (Bill entity : entityList) {
			BillResponseBody entityResponseBody = new BillResponseBody(entity);
			entityResponseBodyList.add(entityResponseBody);
		}

		BillListResponseBody entityListResponseBody = new BillListResponseBody();

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

		Bill bill = retrieveEntity(entityIdString);

		if (Long.compare(bill.getConsumptionIdentifier().getCompany()
				.getCompanyId(), user.getCompany().getCompanyId()) != 0) {
			throw new ForbiddenException();
		}

		BillResponseBody entityResponseBody = new BillResponseBody(bill);

		return Response.ok(gson.toJson(entityResponseBody)).build();
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

		Bill bill = retrieveEntity(entityIdString);

		if (Long.compare(bill.getConsumptionIdentifier().getCompany()
				.getCompanyId(), user.getCompany().getCompanyId()) != 0) {
			throw new ForbiddenException();
		}

		try {
			billDAO.delete(bill);
			billDAO.flush();
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
			URISyntaxException, UnauthorizedException, ConflictException {

		User user = getRequestUser(loginToken);

		Bill bill = retrieveEntity(entityIdString);

		if (Long.compare(bill.getConsumptionIdentifier().getCompany()
				.getCompanyId(), user.getCompany().getCompanyId()) != 0) {
			throw new ForbiddenException();
		}

		BillRequestBody entityRequestBody = retrieveEntityRequestBody(requestBody);
		
		validateStatusValue(entityRequestBody.getStatus());

		if (Long.compare(bill.getConsumptionIdentifier()
				.getConsumptionIdentifierId(), entityRequestBody
				.getConsumptionIdentifierId()) != 0) {

			ConsumptionIdentifier consumptionIdentifier = consumptionIdentifierDAO
					.get(entityRequestBody.getConsumptionIdentifierId());

			if (Long.compare(consumptionIdentifier.getCompany().getCompanyId(),
					user.getCompany().getCompanyId()) != 0) {
				throw new ForbiddenException();
			}

			if (StringUtils.equals(entityRequestBody.getStatus(),
					Status.OPEN.toString())) {

				logger.debug("Checking if there is any open bill for same consumption identifier");

				List<Bill> existingBillList = billDAO
						.listBillsByConsumptionIdentifierIdAndStatus(
								consumptionIdentifier
										.getConsumptionIdentifierId(),
								Status.OPEN.toString());

				if (existingBillList.size() > 0) {
					throw new ConflictException(
							"Consumption identifier already has a open bill");
				}
			}

			bill.setConsumptionIdentifier(consumptionIdentifier);
		}

		bill.setStatus(entityRequestBody.getStatus());
		
		if(null != entityRequestBody.getOpenTimestamp()){
		bill.setOpenTimestamp(entityRequestBody.getOpenTimestamp());
		}
		
		if(null != entityRequestBody.getCloseTimestamp()){
			bill.setCloseTimestamp(entityRequestBody.getCloseTimestamp());
		}

		if (StringUtils.equals(bill.getStatus(), Status.CLOSED.toString())
				&& null == bill.getCloseTimestamp()) {
			bill.setCloseTimestamp(System.currentTimeMillis() / 1000);
		}

		logger.debug("Updating bill");

		try {
			billDAO.update(bill);
			billDAO.flush();
		} catch (ConstraintViolationException e) {
			throw new BadRequestException(MSG_DUPLICATE_ENTITY);
		}

		logger.debug("Building response");

		URI locationURI = new URI(BaseResource.getServerBasePath()
				+ BaseResource.SLASH + PATH + BaseResource.SLASH
				+ bill.getBillId());

		logger.debug("Finished processing request successfully");

		return Response.ok().contentLocation(locationURI).build();
	}

	private Bill retrieveEntity(String entityIdString)
			throws BadRequestException, NotFoundException {
		Long entityId;

		logger.debug("Retrieving bill from id parameter");
		logger.debug("Converting bill id from string to Long");

		try {
			entityId = Long.parseLong(entityIdString);
		} catch (NumberFormatException e) {
			logger.debug("Malformed bill id: " + entityIdString);
			throw new BadRequestException(MSG_INVALID_ENTITY_IDENTIFIER);
		}

		Bill bill = billDAO.get(entityId);

		if (null == bill) {
			throw new NotFoundException();
		}

		return bill;
	}

	private BillRequestBody retrieveEntityRequestBody(String requestBodyString)
			throws BadRequestException {
		BillRequestBody entityRequestBody;

		try {
			entityRequestBody = gson.fromJson(requestBodyString,
					BillRequestBody.class);
		} catch (JsonSyntaxException e) {
			throw new BadRequestException(MSG_INVALID_JSON_AS_REQUEST_BODY);
		}

		if (null == entityRequestBody) {
			throw new BadRequestException(MSG_REQUEST_BODY_MISSING_OR_BLANK);
		}

		entityRequestBody.validate();

		return entityRequestBody;
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

	private void validateStatusValue(String statusValue)
			throws BadRequestException {
		Status[] statusArray = Status.values();
		boolean found = false;

		for (Status status : statusArray) {
			if (StringUtils.equals(status.toString(), statusValue)) {
				found = true;
				break;
			}
		}

		if (!found) {
			logger.debug("Invalid status parameter");
			throw new BadRequestException("Invalid status parameter value");
		}
	}

}
