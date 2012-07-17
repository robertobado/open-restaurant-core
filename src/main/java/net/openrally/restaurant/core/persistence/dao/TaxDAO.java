package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.persistence.entity.Tax;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("taxDAO")
@Transactional
public class TaxDAO extends AbstractHibernateDAO<Tax, Long> {

	@Override
	protected Class<Tax> getDomainClass() {
		return Tax.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tax> getAllByCompanyId(Long companyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from "
				+ domainClass.getSimpleName() + " where companyId = :companyId ");
		query.setParameter("companyId", companyId);
	    return query.list();
	}

}
