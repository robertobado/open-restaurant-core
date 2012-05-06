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
@Table(name = "Bill")
public class Bill implements Serializable{

	private static final long serialVersionUID = -8324537690167322235L;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	private Long billId;
	
	@ManyToOne(optional=false, fetch = FetchType.LAZY)
	@JoinColumn(name = "consumptionIdentifierId", referencedColumnName = "consumptionIdentifierId", nullable=false)
	private ConsumptionIdentifier consumptionIdentifier;
	
	@Column(nullable=false)
	private String status;
	
	@Column(nullable=false)
	private Long openTimestamp;
	
	@Column
	private Long closeTimestamp;

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}

	public ConsumptionIdentifier getConsumptionIdentifier() {
		return consumptionIdentifier;
	}

	public void setConsumptionIdentifier(ConsumptionIdentifier consumptionIdentifier) {
		this.consumptionIdentifier = consumptionIdentifier;
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
}
