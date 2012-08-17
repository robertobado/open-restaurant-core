package net.openrally.restaurant.core.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "company", uniqueConstraints = {@UniqueConstraint(columnNames={"companyName"})})
public class Company implements Serializable{

	private static final long serialVersionUID = 8620274543674208772L;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	private Long companyId;
	
	@Column(nullable=false)
	private String companyName;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
}
