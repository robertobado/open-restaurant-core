package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.persistence.entity.Permission;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("permissionDAO")
@Transactional
public class PermissionDAO extends AbstractHibernateDAO<Permission, Long> {

	@Override
	protected Class<Permission> getDomainClass() {
		return Permission.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Permission> listByRoleId(long roleId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from "
				+ domainClass.getSimpleName() + " where roleId = :roleId ");
		query.setParameter("roleId", roleId);
	    return query.list();
	}
}
