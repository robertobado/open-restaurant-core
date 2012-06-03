package net.openrally.restaurant.core.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bill_item")
public class BillItem implements Serializable{

	private static final long serialVersionUID = 1601860130482671796L;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	private Long billItemId;
	
	@ManyToOne(optional=false, fetch = FetchType.LAZY)
	@JoinColumn(name = "billId", referencedColumnName = "billId", nullable=false)
	private Bill bill;
	
	@ManyToOne(optional=false, fetch = FetchType.LAZY)
	@JoinColumn(name = "productId", referencedColumnName = "productId", nullable=false)
	private Product product;
	
	@Column(nullable=false)
	private Double quantity;
	
	@Column(nullable=false)
	private Double unitPrice;

	public Long getBillItemId() {
		return billItemId;
	}

	public void setBillItemId(Long billItemId) {
		this.billItemId = billItemId;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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
	
}
