package net.openrally.restaurant.core.response.body;

import net.openrally.restaurant.core.persistence.entity.BillItem;

public class BillItemResponseBody {

	private Long billItemId;
	private Long billId;
	private Long productId;
	private Double quantity;
	private Double unitPrice;
	
	public BillItemResponseBody(BillItem billItem) {
		this.billItemId = billItem.getBillItemId();
		this.billId = billItem.getBill().getBillId();
		this.productId = billItem.getProduct().getProductId();
		this.quantity = billItem.getQuantity();
		this.unitPrice = billItem.getUnitPrice();
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
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
	    
	    if(Long.compare(otherEntityResponseBody.getBillItemId(), this.getBillItemId()) != 0){
	    	return false;
	    }
	    if(Long.compare(otherEntityResponseBody.getBillId(), this.getBillId()) != 0){
	    	return false;
	    }
	    if(Long.compare(otherEntityResponseBody.getProductId(), this.getProductId()) != 0){
	    	return false;
	    }
	    if(Double.compare(otherEntityResponseBody.getQuantity(), this.getQuantity()) != 0){
	    	return false;
	    }
	    if(Double.compare(otherEntityResponseBody.getUnitPrice(), this.getUnitPrice()) != 0){
	    	return false;
	    }
	    return true;
	}
}
