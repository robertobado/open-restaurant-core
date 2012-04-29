package net.openrally.restaurant.core.persistence.dao;

import net.openrally.restaurant.core.persistence.entity.Role;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository("roleDAO")
@Transactional
public class RoleDAO extends AbstractHibernateDAO<Role, Long> {
	
	@Override
	protected Class<Role> getDomainClass() {
		return Role.class;
	}

}
