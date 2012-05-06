package net.openrally.restaurant.core.response.body;

import org.apache.commons.lang.StringUtils;

import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;

public class ConsumptionIdentifierResponseBody {
	
	private Long consumptionIdentifierId; 
	private String identifier;
	private String description;
	
	public ConsumptionIdentifierResponseBody(ConsumptionIdentifier consumptionIdentifier) {
		this.consumptionIdentifierId = consumptionIdentifier.getConsumptionIdentifierId();
		this.identifier = consumptionIdentifier.getIdentifier();
		this.description = consumptionIdentifier.getDescription();
	}
	public Long getConsumptionIdentifierId() {
		return consumptionIdentifierId;
	}
	public void setConsumptionIdentifierId(Long consumptionIdentifierId) {
		this.consumptionIdentifierId = consumptionIdentifierId;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
	    if (!(other instanceof ConsumptionIdentifierResponseBody)) return false;
	    ConsumptionIdentifierResponseBody otherEntityBody = (ConsumptionIdentifierResponseBody)other;
	    
	    if(otherEntityBody.getConsumptionIdentifierId() != this.getConsumptionIdentifierId()){
	    	return false;
	    }
	    if(!StringUtils.equals(otherEntityBody.getIdentifier(), this.getIdentifier())){
	    	return false;
	    }
	    if(!StringUtils.equals(otherEntityBody.getDescription(), this.getDescription())){
	    	return false;
	    }
	    return true;
	}
}
