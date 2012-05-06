package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.persistence.entity.Role;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository("roleDAO")
@Transactional
public class RoleDAO extends AbstractHibernateDAO<Role, Long> {
	
	@Override
	protected Class<Role> getDomainClass() {
		return Role.class;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getAllByCompanyId(Long companyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from "
				+ domainClass.getSimpleName() + " where companyId = :companyId ");
		query.setParameter("companyId", companyId);
	    return query.list();
	}

}
