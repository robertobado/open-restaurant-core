package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.Configuration;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("configurationDAO")
@Transactional
public class ConfigurationDAO extends
		AbstractHibernateDAO<Configuration, Integer> {
	@Override
	protected Class<Configuration> getDomainClass() {
		return Configuration.class;
	}

	@SuppressWarnings("unchecked")
	public Configuration loadByCompanyId(String companyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from " + getDomainClass()
				+ " where id_company = :id_company ");
		query.setParameter("id_company", companyId);
		List<Configuration> list = query.list();
		if (null != list && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
