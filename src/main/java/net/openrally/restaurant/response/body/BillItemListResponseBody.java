package net.openrally.restaurant.response.body;

import java.util.List;

public class BillItemListResponseBody {

	private List<BillItemResponseBody> list;

	public List<BillItemResponseBody> getList() {
		return list;
	}

	public void setList(List<BillItemResponseBody> list) {
		this.list = list;
	}
}
