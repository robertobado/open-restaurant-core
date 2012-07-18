package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.openrally.restaurant.core.persistence.entity.Bill;
import net.openrally.restaurant.core.persistence.entity.BillItem;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;

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
				.createQuery("select bi from "
						+ domainClass.getSimpleName()
						+ " bi, "
						+ Bill.class.getSimpleName()
						+" b, "
						+ ConsumptionIdentifier.class.getSimpleName()
						+" ci, "
						+ Company.class.getSimpleName()
						+" c where c.companyId = :companyId AND ci.company = c AND b.consumptionIdentifier = ci AND b.billId = :billId AND bi.bill = b");
		query.setParameter("companyId", companyId);
		query.setParameter("billId", billId);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<BillItem> getAllByReferenceIdAndType(Long referenceId, String string) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("from "
						+ domainClass.getSimpleName()
						+ " where referenceId = :referenceId AND type = :type");
		query.setParameter("referenceId", referenceId);
		query.setParameter("type", string);
		return query.list();
	}

}
