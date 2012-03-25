package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.persistence.entity.User;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("userDAO")
@Transactional
public class UserDAO extends AbstractHibernateDAO<User, Integer>{
	@Override
	protected Class<User> getDomainClass() {
		return User.class;
	}
	
	@SuppressWarnings("unchecked")
	public User loadByCompanyIdAndLogin(String companyId, String login) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from " + getDomainClass()
				+ " where id_company = :id_company and login = :login");
		query.setParameter("id_company", companyId);
		query.setParameter("login", login);
		List<User> list = query.list();
		if (null != list && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
