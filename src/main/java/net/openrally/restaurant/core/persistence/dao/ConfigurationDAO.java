package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.exception.InternalServerErrorException;
import net.openrally.restaurant.core.persistence.entity.Configuration;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("configurationDAO")
@Transactional
public class ConfigurationDAO extends
		AbstractHibernateDAO<Configuration, Long> {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	protected Class<Configuration> getDomainClass() {
		return Configuration.class;
	}

	@SuppressWarnings("unchecked")
	public Configuration loadByCompanyId(Long companyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from " + getDomainClass().getSimpleName()
				+ " where companyId = :companyId ");
		query.setParameter("companyId", companyId);
		List<Configuration> list = query.list();
		
		if(list == null || list.size() == 0){
			return null;
		}
		else if (list.size() == 1){
			return list.get(0);
		}
		else{
			logger.error(String.format("Multiple entries found while querying configuration for companyId = [%d] ", companyId));
			throw new InternalServerErrorException();
		}
	}
}
