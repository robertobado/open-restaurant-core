package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.persistence.entity.Product;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("productDAO")
@Transactional
public class ProductDAO extends AbstractHibernateDAO<Product, Long> {

	@Override
	protected Class<Product> getDomainClass() {
		
		return Product.class;
	}

	@SuppressWarnings("unchecked")
	public List<Product> getAllByCompanyId(long companyId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from "
				+ domainClass.getSimpleName() + " where companyId = :companyId ");
		query.setParameter("companyId", companyId);
	    return query.list();
	}
	
}
