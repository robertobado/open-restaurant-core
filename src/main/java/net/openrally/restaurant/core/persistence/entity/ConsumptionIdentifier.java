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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "consumption_identifier", uniqueConstraints = {@UniqueConstraint(columnNames={"companyId", "identifier"})})
public class ConsumptionIdentifier implements Serializable{

	private static final long serialVersionUID = -5715929482947162106L;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	private Long consumptionIdentifierId;
	
	@ManyToOne(optional=false, fetch = FetchType.LAZY)
	@JoinColumn(name = "companyId", referencedColumnName = "companyId", nullable=false)
	private Company company;
	
	@Column(nullable=false)
	private String identifier;
	
	@Column
	private String description;

	public Long getConsumptionIdentifierId() {
		return consumptionIdentifierId;
	}

	public void setConsumptionIdentifierId(Long consumptionIdentifierId) {
		this.consumptionIdentifierId = consumptionIdentifierId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
