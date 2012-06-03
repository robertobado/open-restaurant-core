package net.openrally.restaurant.response.body;

import org.apache.commons.lang.StringUtils;

import net.openrally.restaurant.core.persistence.entity.Bill;

public class BillResponseBody {
	
	private Long billId;
	private Long consumptionIdentifierId;
	private String status;
	private Long openTimestamp;
	private Long closeTimestamp;
	
	public BillResponseBody(Bill bill) {
		this.billId = bill.getBillId();
		this.consumptionIdentifierId = bill.getConsumptionIdentifier().getConsumptionIdentifierId();
		this.status = bill.getStatus();
		this.openTimestamp = bill.getOpenTimestamp();
		this.closeTimestamp = bill.getCloseTimestamp();
	}
	public Long getBillId() {
		return billId;
	}
	public void setBillId(Long billId) {
		this.billId = billId;
	}
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
	
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof BillResponseBody)) return false;
	    BillResponseBody otherEntityResponseBody = (BillResponseBody)other;
	    
	    if(null == otherEntityResponseBody.getBillId() || !otherEntityResponseBody.getBillId().equals(this.getBillId())){
	    	return false;
	    }
	    if (otherEntityResponseBody.getOpenTimestamp() == null && this.getOpenTimestamp() != null){
	    	return false;
	    }
	    if (this.getOpenTimestamp() == null && otherEntityResponseBody.getOpenTimestamp() != null){
	    	return false;
	    }
	    if((otherEntityResponseBody.getOpenTimestamp() != null && this.getOpenTimestamp() != null) && !otherEntityResponseBody.getOpenTimestamp().equals(this.getOpenTimestamp())){
	    	return false;
	    }
	    if (otherEntityResponseBody.getCloseTimestamp() == null && this.getCloseTimestamp() != null){
	    	return false;
	    }
	    if (this.getCloseTimestamp() == null && otherEntityResponseBody.getCloseTimestamp() != null){
	    	return false;
	    }
	    if((otherEntityResponseBody.getCloseTimestamp() != null && this.getCloseTimestamp() != null) && !otherEntityResponseBody.getCloseTimestamp().equals(this.getCloseTimestamp())){
	    	return false;
	    }
	    if(!StringUtils.equals(otherEntityResponseBody.getStatus(), this.getStatus())){
	    	return false;
	    }
	    if(null == otherEntityResponseBody.getConsumptionIdentifierId() || !otherEntityResponseBody.getConsumptionIdentifierId().equals(this.getConsumptionIdentifierId())){
	    	return false;
	    }
	    return true;
	}

}
