package net.openrally.restaurant.core.persistence.entity;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import net.openrally.restaurant.core.persistence.dao.UserDAO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/applicationContext.xml" })
public class UserTest {
	private UserDAO dao;

	@Autowired
	public void setDao(UserDAO dao) {
		this.dao = dao;
	}

	public User createUser() {
		User user = new User();
		dao.saveUser(user);
		return user;
	}

	public void deleteUser(User user) {
		dao.deleteUser(user);
	}

	@Test
	public void testCreateData() {
		User user = createUser();
		Assert.assertEquals(1, dao.getAllUser().size());
		deleteUser(user);
	}

	@Test
	public void testRetrieveData() {
		int numberOfUsers = 10;
		List<User> createdUsers = new LinkedList<User>();

		for (int i = 0; i < numberOfUsers; i++) {
			createdUsers.add(createUser());
		}

		List<User> retrievedUserList = dao.getAllUser();
		
		Assert.assertEquals(numberOfUsers, retrievedUserList.size());
		Assert.assertTrue(retrievedUserList.containsAll(createdUsers));
	}

	@Test
	public void testUpdateData() {
		List<User> userList = dao.getAllUser();
		Assert.assertEquals(1, userList.size());
		User userExpected = userList.get(0);
		dao.saveUser(userExpected);
		User userResult = dao.selectUserById(userExpected.getUserId());
	}

	@Test
	public void testDeleteData() {
		List<User> userList = dao.getAllUser();
		Assert.assertEquals(1, userList.size());
		User userExpected = userList.get(0);
		dao.deleteUser(userExpected);
		User userResult = dao.selectUserById(userExpected.getUserId());
		Assert.assertEquals(userResult, null);
	}

	private SecureRandom random = new SecureRandom();

	public String nextRandom() {
		return new BigInteger(130, random).toString(32);
	}
}
