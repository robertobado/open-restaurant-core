package net.openrally.restaurant.response.body;

import org.apache.commons.lang.StringUtils;

import net.openrally.restaurant.core.persistence.entity.Tax;

public class TaxResponseBody {

	private Long taxId;
	private String name;
	private String description;
	private Double amount;
	private Boolean percentage;
	
	public TaxResponseBody(Tax tax) {
		taxId = tax.getTaxId();
		name = tax.getName();
		description = tax.getDescription();
		amount = tax.getAmount();
		percentage = tax.getPercentage();
		
	}
	public Long getTaxId() {
		return taxId;
	}
	public void setTaxId(Long taxId) {
		this.taxId = taxId;
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
	
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof TaxResponseBody)) return false;
	    TaxResponseBody otherEntityResponseBody = (TaxResponseBody)other;
	    
	    if(null == otherEntityResponseBody.getTaxId() || !otherEntityResponseBody.getTaxId().equals(this.getTaxId())){
	    	return false;
	    }
	    if(!StringUtils.equals(otherEntityResponseBody.getName(), this.getName())){
	    	return false;
	    }
	    if(!StringUtils.equals(otherEntityResponseBody.getDescription(), this.getDescription())){
	    	return false;
	    }
	    if (this.getAmount() == null && otherEntityResponseBody.getAmount() != null){
	    	return false;
	    }
	    if((otherEntityResponseBody.getAmount() != null && this.getAmount() != null) && !otherEntityResponseBody.getAmount().equals(this.getAmount())){
	    	return false;
	    }
	    if (this.getPercentage() == null && otherEntityResponseBody.getPercentage() != null){
	    	return false;
	    }
	    if((otherEntityResponseBody.getPercentage() != null && this.getPercentage() != null) && !otherEntityResponseBody.getPercentage().equals(this.getPercentage())){
	    	return false;
	    }
	    return true;
	}

}
