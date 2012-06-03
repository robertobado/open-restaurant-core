package net.openrally.restaurant.response.body;

import java.util.List;

public class ProductListResponseBody {

	private List<ProductResponseBody> list;

	public List<ProductResponseBody> getList() {
		return list;
	}

	public void setList(List<ProductResponseBody> list) {
		this.list = list;
	}
}
