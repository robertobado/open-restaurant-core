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
@Table(name = "permission", uniqueConstraints = {@UniqueConstraint(columnNames={"roleId", "path"})})
public class Permission implements Serializable{

	private static final long serialVersionUID = -1667682774162763540L;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	private Long permissionId;
	
	@ManyToOne(optional=false, fetch = FetchType.LAZY)
	@JoinColumn(name = "roleId", referencedColumnName = "roleId", nullable=false)
	private Role role;
	
	@Column(nullable=false)
	private String path;
	
	@Column(nullable=false)
	private boolean allowPost;
	
	@Column(nullable=false)
	private boolean allowPut;
	
	@Column(nullable=false)
	private boolean allowGet;
	
	@Column(nullable=false)
	private boolean allowDelete;

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isAllowPost() {
		return allowPost;
	}

	public void setAllowPost(boolean allowPost) {
		this.allowPost = allowPost;
	}

	public boolean isAllowPut() {
		return allowPut;
	}

	public void setAllowPut(boolean allowPut) {
		this.allowPut = allowPut;
	}

	public boolean isAllowGet() {
		return allowGet;
	}

	public void setAllowGet(boolean allowGet) {
		this.allowGet = allowGet;
	}

	public boolean isAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(boolean allowDelete) {
		this.allowDelete = allowDelete;
	}
	
}
