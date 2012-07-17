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
@Table(name = "tax", uniqueConstraints = {@UniqueConstraint(columnNames={"companyId", "name"})})
public class Tax implements Serializable{
	
	private static final long serialVersionUID = -2906164296742307689L;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	private Long taxId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "companyId", referencedColumnName = "companyId", nullable = false)
	private Company company;
	
	@Column(nullable=false)
	private String name;
	
	@Column
	private String description;
	
	@Column(nullable=false)
	private Double amount;
	
	@Column(nullable=false)
	private Boolean percentage;

	public Long getTaxId() {
		return taxId;
	}

	public void setTaxId(Long taxId) {
		this.taxId = taxId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Boolean getPercentage() {
		return percentage;
	}

	public void setPercentage(Boolean percentage) {
		this.percentage = percentage;
	}

}
