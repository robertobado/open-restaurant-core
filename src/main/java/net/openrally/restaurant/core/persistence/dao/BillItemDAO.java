package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.openrally.restaurant.core.persistence.entity.BillItem;

@Repository("billItemDAO")
@Transactional
public class BillItemDAO extends AbstractHibernateDAO<BillItem, Long> {

	@Override
	protected Class<BillItem> getDomainClass() {
		return BillItem.class;
	}

	@SuppressWarnings("unchecked")
	public List<BillItem> getAllByCompanyIdAndBillId(Long companyId, Long billId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("from "
						+ domainClass.getSimpleName()
						+ " where companyId = :companyId AND billId = :billId");
		query.setParameter("companyId", companyId);
		query.setParameter("billId", billId);
		return query.list();
	}

}
