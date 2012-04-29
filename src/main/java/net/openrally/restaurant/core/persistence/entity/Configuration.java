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
@Table(name = "configuration")
public class Configuration implements Serializable {

	private static final long serialVersionUID = -365181466141881983L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	private long configurationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "companyId", referencedColumnName = "companyId", nullable = false)
	private Company company;

	@Column(nullable = false)
	private String hashSalt;
	
	@Column(nullable = false)
	private long loginTokenLifeTime;

	public long getLoginTokenLifeTime() {
		return loginTokenLifeTime;
	}

	public void setLoginTokenLifeTime(long loginTokenLifeTime) {
		this.loginTokenLifeTime = loginTokenLifeTime;
	}

	public long getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(long configurationId) {
		this.configurationId = configurationId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getHashSalt() {
		return hashSalt;
	}

	public void setHashSalt(String hashSalt) {
		this.hashSalt = hashSalt;
	}

}