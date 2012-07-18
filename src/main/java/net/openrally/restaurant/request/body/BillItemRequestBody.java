package net.openrally.restaurant.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;
import net.openrally.restaurant.core.annotation.ParameterValuePositive;


public class BillItemRequestBody extends BaseRequestBody{

	@ParameterRequired
	@ParameterValuePositive
	private Long billId;
	
	@ParameterRequired
	@ParameterValuePositive
	private Long referenceId;
	
	@ParameterRequired
	@ParameterValuePositive
	private Double quantity;

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
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
}
