package net.openrally.restaurant.core.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="city")
public class City implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5769341332841834916L;

	private long id;

	private String name;

	public City(String name) {
		super();
		this.name = name;
	}

	public City() {}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(nullable=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
