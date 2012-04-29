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

import net.openrally.restaurant.core.utils.HashCalculator;

@Entity
@Table(name = "User", uniqueConstraints = {@UniqueConstraint(columnNames={"companyId", "login"})})
public class User implements Serializable {

	private static final String SLASH = "/";

	private static final long serialVersionUID = 8496087166198616020L;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	private long userId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "companyId", referencedColumnName = "companyId", nullable=false)
	private Company company;
	
	@Column(nullable=false)
	private String login;
	
	@Column(nullable=false)
	private String passwordHash;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public static String generatePasswordHash(String password, String salt){
		String passwordWithSalt = salt + SLASH
				+ password;
		return HashCalculator.generateMD5Hash(passwordWithSalt);
	}
}