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
	
	public enum Type {
	    PRODUCT("Product"),
	    TAX("Tax");
	    
	    private String type;
	    
	    Type(String type){
	    	this.type = type;
	    }
	    
	    public String toString(){
	    	return type;
	    }
	}

	private static final long serialVersionUID = 1601860130482671796L;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	private Long billItemId;
	
	@ManyToOne(optional=false, fetch = FetchType.LAZY)
	@JoinColumn(name = "billId", referencedColumnName = "billId", nullable=false)
	private Bill bill;
	
	@Column(nullable=false)
	private String type;
	
	@Column(nullable=false)
	private Long referenceId;
	
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
	
}
