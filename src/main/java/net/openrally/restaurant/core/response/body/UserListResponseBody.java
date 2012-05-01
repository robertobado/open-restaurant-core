package net.openrally.restaurant.core.response.body;

import java.util.List;

public class UserListResponseBody {
	List<UserResponseBody> list;

	public List<UserResponseBody> getList() {
		return list;
	}

	public void setList(List<UserResponseBody> list) {
		this.list = list;
	}
}
