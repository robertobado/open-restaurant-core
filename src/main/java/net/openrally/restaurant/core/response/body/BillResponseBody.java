package net.openrally.restaurant.core.response.body;

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
	    
	    if(Long.compare(otherEntityResponseBody.getBillId(), this.getBillId()) != 0){
	    	return false;
	    }
	    if (otherEntityResponseBody.getOpenTimestamp() == null && this.getOpenTimestamp() != null){
	    	return false;
	    }
	    if (this.getOpenTimestamp() == null && otherEntityResponseBody.getOpenTimestamp() != null){
	    	return false;
	    }
	    if((otherEntityResponseBody.getOpenTimestamp() != null && this.getOpenTimestamp() != null) && Long.compare(otherEntityResponseBody.getOpenTimestamp(), this.getOpenTimestamp()) != 0){
	    	return false;
	    }
	    if (otherEntityResponseBody.getCloseTimestamp() == null && this.getCloseTimestamp() != null){
	    	return false;
	    }
	    if (this.getCloseTimestamp() == null && otherEntityResponseBody.getCloseTimestamp() != null){
	    	return false;
	    }
	    if((otherEntityResponseBody.getCloseTimestamp() != null && this.getCloseTimestamp() != null) && Long.compare(otherEntityResponseBody.getCloseTimestamp(), this.getCloseTimestamp()) != 0){
	    	return false;
	    }
	    if(!StringUtils.equals(otherEntityResponseBody.getStatus(), this.getStatus())){
	    	return false;
	    }
	    if(Long.compare(otherEntityResponseBody.getConsumptionIdentifierId(),this.getConsumptionIdentifierId()) != 0){
	    	return false;
	    }
	    return true;
	}

}
