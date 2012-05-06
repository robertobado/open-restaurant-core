package net.openrally.restaurant.core.response.body;

import org.apache.commons.lang.StringUtils;

import net.openrally.restaurant.core.persistence.entity.Product;

public class ProductResponseBody {
	
	private long productId;
	private String name;
	private String description;
	
	public ProductResponseBody(Product product) {
		this.productId = product.getProductId();
		this.name = product.getName();
		this.description = product.getDescription();
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
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
	
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof ProductResponseBody)) return false;
	    ProductResponseBody otherEntityBody = (ProductResponseBody)other;
	    
	    if(otherEntityBody.getProductId() != this.getProductId()){
	    	return false;
	    }
	    if(!StringUtils.equals(otherEntityBody.getName(), this.getName())){
	    	return false;
	    }
	    if(!StringUtils.equals(otherEntityBody.getDescription(), this.getDescription())){
	    	return false;
	    }
	    return true;
	}

}
