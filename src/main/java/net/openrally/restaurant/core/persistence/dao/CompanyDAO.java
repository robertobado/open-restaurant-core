package net.openrally.restaurant.core.persistence.dao;

import net.openrally.restaurant.core.persistence.entity.Company;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("companyDAO")
@Transactional
public class CompanyDAO extends AbstractHibernateDAO<Company, Integer> {
	
	@Override
	protected Class<Company> getDomainClass() {
		return Company.class;
	}
}
