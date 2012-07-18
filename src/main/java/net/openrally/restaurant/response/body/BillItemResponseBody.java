package net.openrally.restaurant.response.body;

import org.apache.commons.lang.StringUtils;

import net.openrally.restaurant.core.persistence.entity.BillItem;

public class BillItemResponseBody {

	private Long billItemId;
	private Long billId;
	private String type;
	private Long referenceId;
	private Double quantity;
	private Double unitPrice;
	
	public BillItemResponseBody(BillItem billItem) {
		this.billItemId = billItem.getBillItemId();
		this.billId = billItem.getBill().getBillId();
		this.referenceId = billItem.getReferenceId();
		this.quantity = billItem.getQuantity();
		this.unitPrice = billItem.getUnitPrice();
		this.type = billItem.getType();
	}
	public Long getBillItemId() {
		return billItemId;
	}
	public void setBillItemId(Long billItemId) {
		this.billItemId = billItemId;
	}
	public Long getBillId() {
		return billId;
	}
	public void setBillId(Long billId) {
		this.billId = billId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public Double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof BillItemResponseBody)) return false;
	    BillItemResponseBody otherEntityResponseBody = (BillItemResponseBody)other;
	    
	    if(null == otherEntityResponseBody.getBillItemId() || !otherEntityResponseBody.getBillItemId().equals(this.getBillItemId())){
	    	return false;
	    }
	    if(null == otherEntityResponseBody.getBillId() || !otherEntityResponseBody.getBillId().equals(this.getBillId())){
	    	return false;
	    }
	    if(null == otherEntityResponseBody.getReferenceId() || !otherEntityResponseBody.getReferenceId().equals(this.referenceId)){
	    	return false;
	    }
	    if(!StringUtils.equals(type, otherEntityResponseBody.getType())){
	    	return false;
	    }
	    if(null == otherEntityResponseBody.getQuantity() || !otherEntityResponseBody.getQuantity().equals(this.getQuantity())){
	    	return false;
	    }
	    if(null == otherEntityResponseBody.getUnitPrice() || !otherEntityResponseBody.getUnitPrice().equals(this.getUnitPrice())){
	    	return false;
	    }
	    return true;
	}
}
