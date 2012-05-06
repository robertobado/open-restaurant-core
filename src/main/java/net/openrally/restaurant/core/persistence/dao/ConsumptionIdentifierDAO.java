package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("consumptionIdentifierDAO")
@Transactional
public class ConsumptionIdentifierDAO extends AbstractHibernateDAO<ConsumptionIdentifier, Long>{

	@Override
	protected Class<ConsumptionIdentifier> getDomainClass() {
		return ConsumptionIdentifier.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsumptionIdentifier> getAllByCompanyId(Long companyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from "
				+ domainClass.getSimpleName() + " where companyId = :companyId ");
		query.setParameter("companyId", companyId);
	    return query.list();
	}

}
