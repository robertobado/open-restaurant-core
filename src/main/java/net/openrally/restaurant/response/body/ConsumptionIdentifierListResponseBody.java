package net.openrally.restaurant.response.body;

import java.util.List;

public class ConsumptionIdentifierListResponseBody {
	List<ConsumptionIdentifierResponseBody> list;

	public List<ConsumptionIdentifierResponseBody> getList() {
		return list;
	}

	public void setList(List<ConsumptionIdentifierResponseBody> list) {
		this.list = list;
	}
}
