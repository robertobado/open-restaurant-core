package net.openrally.restaurant.core.request.body;

import net.openrally.restaurant.core.annotation.ParameterRequired;
import net.openrally.restaurant.core.annotation.ParameterValuePositive;


public class BillItemRequestBody extends BaseRequestBody{

	@ParameterRequired
	@ParameterValuePositive
	private Long billId;
	
	@ParameterRequired
	@ParameterValuePositive
	private Long productId;
	
	@ParameterRequired
	@ParameterValuePositive
	private Double quantity;

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
}
