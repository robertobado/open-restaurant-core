package net.openrally.restaurant.response.body;

import java.util.List;

public class PermissionListResponseBody {
	List<PermissionResponseBody> list;

	public List<PermissionResponseBody> getList() {
		return list;
	}

	public void setList(List<PermissionResponseBody> list) {
		this.list = list;
	}
}
