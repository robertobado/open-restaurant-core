package net.openrally.restaurant.response.body;

import java.util.List;

public class BillListResponseBody {

	private List<BillResponseBody> list;

	public List<BillResponseBody> getList() {
		return list;
	}

	public void setList(List<BillResponseBody> list) {
		this.list = list;
	}
}
