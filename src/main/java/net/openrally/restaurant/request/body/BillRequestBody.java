package net.openrally.restaurant.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;
import net.openrally.restaurant.core.annotation.ParameterValuePositive;

public class BillRequestBody extends BaseRequestBody{
	
	@ParameterRequired
	@ParameterValuePositive
	private Long consumptionIdentifierId;
	
	@ParameterRequired
	private String status;
	
	private Long openTimestamp;
	
	private Long closeTimestamp;
	
	private Integer pax;
	
	public Long getConsumptionIdentifierId() {
		return consumptionIdentifierId;
	}
	public void setConsumptionIdentifierId(Long consumptionIdentifierId) {
		this.consumptionIdentifierId = consumptionIdentifierId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getOpenTimestamp() {
		return openTimestamp;
	}
	public void setOpenTimestamp(Long openTimestamp) {
		this.openTimestamp = openTimestamp;
	}
	public Long getCloseTimestamp() {
		return closeTimestamp;
	}
	public void setCloseTimestamp(Long closeTimestamp) {
		this.closeTimestamp = closeTimestamp;
	}
	public Integer getPax() {
		return pax;
	}
	public void setPax(Integer pax) {
		this.pax = pax;
	}
	
}
