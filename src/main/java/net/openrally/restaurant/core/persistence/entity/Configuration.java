package net.openrally.restaurant.core.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "configuration")
public class Configuration implements Serializable {

	private static final long serialVersionUID = -365181466141881983L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	private Long configurationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "companyId", referencedColumnName = "companyId", nullable = false)
	private Company company;

	@Column(nullable = false)
	private String hashSalt;
	
	@Column(nullable = false)
	private Long loginTokenLifeTime;
	
	@Column(nullable = false)
	private String companyName;
	
	@Column(nullable = false)
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String billTemplate;

	public Long getLoginTokenLifeTime() {
		return loginTokenLifeTime;
	}

	public void setLoginTokenLifeTime(Long loginTokenLifeTime) {
		this.loginTokenLifeTime = loginTokenLifeTime;
	}

	public Long getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(Long configurationId) {
		this.configurationId = configurationId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBillTemplate() {
		return billTemplate;
	}

	public void setBillTemplate(String billTemplate) {
		this.billTemplate = billTemplate;
	}

	public String getHashSalt() {
		return hashSalt;
	}

	public void setHashSalt(String hashSalt) {
		this.hashSalt = hashSalt;
	}

}
