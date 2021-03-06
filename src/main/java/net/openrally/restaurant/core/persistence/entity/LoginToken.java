package net.openrally.restaurant.core.persistence.entity;

import java.io.Serializable;
import java.util.UUID;

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
@Table(name = "login_token", uniqueConstraints = {@UniqueConstraint(columnNames={"token"})})
public class LoginToken implements Serializable{

	private static final long serialVersionUID = -5506103645468557836L;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	private Long tokenId;
	
	@Column(nullable = false)
	private String token;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", referencedColumnName = "userId", nullable=false)
	private User user;
	
	@Column(nullable=false)
	private Long expirationTime;

	public Long getTokenId() {
		return tokenId;
	}

	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Long expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	public void generateToken(){
		this.token = UUID.randomUUID().toString();
	}

}
