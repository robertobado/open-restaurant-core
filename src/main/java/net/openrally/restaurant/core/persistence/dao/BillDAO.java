package net.openrally.restaurant.core.persistence.dao;

import java.util.Iterator;
import java.util.List;

import net.openrally.restaurant.core.persistence.entity.Bill;
import net.openrally.restaurant.core.persistence.entity.Company;
import net.openrally.restaurant.core.persistence.entity.ConsumptionIdentifier;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("billDAO")
@Transactional
public class BillDAO extends AbstractHibernateDAO<Bill, Long> {

	@Override
	protected Class<Bill> getDomainClass() {
		return Bill.class;
	}

	@SuppressWarnings("unchecked")
	public List<Bill> listBillsByConsumptionIdentifierIdAndStatus(
			Long consumptionIdentifierId, String status) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("from "
						+ domainClass.getSimpleName()
						+ " where consumptionIdentifierId = :consumptionIdentifierId AND status = :status");
		query.setParameter("consumptionIdentifierId", consumptionIdentifierId);
		query.setParameter("status", status);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Bill> listBillsByConsumptionIdentifierId(
			Long consumptionIdentifierId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from " + domainClass.getSimpleName()
				+ " where consumptionIdentifierId = :consumptionIdentifierId");
		query.setParameter("consumptionIdentifierId", consumptionIdentifierId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Bill> getAllByCompanyIdWithFilters(Long companyId, Long consumptionIdentifierId,
			Long startOpenTimestamp, Long endOpenTimestamp,
			Long startCloseTimestamp, Long endCloseTimestamp, List<String> statusList) {
		
		String whereClause = " where c.companyId = :companyId AND ci.company = c AND b.consumptionIdentifier = ci ";
		
		if(null != consumptionIdentifierId){
			whereClause += " AND ci.consumptionIdentifierId = :consumptionIdentifierId ";
		}
		
		if(null != startOpenTimestamp){
			whereClause += " AND b.openTimestamp >= :startOpenTimestamp ";
		}
		if(null != endOpenTimestamp){
			whereClause += " AND b.openTimestamp <= :endOpenTimestamp ";
		}
		if(null != startCloseTimestamp){
			whereClause += " AND b.closeTimestamp >= :startCloseTimestamp ";
		}
		if(null != endCloseTimestamp){
			whereClause += " AND b.closeTimestamp <= :endCloseTimestamp ";
		}
		
		if(null != statusList && !statusList.isEmpty()){
			whereClause += " AND ( ";
			
			Iterator<String> iterator = statusList.iterator();
			
			for(int i = 0; iterator.hasNext(); i++){
				iterator.next();
				whereClause += " b.status = :status"+i+" ";
				if(iterator.hasNext()){
					whereClause += " OR ";
				}
			}
			
			
			whereClause += " ) ";
		}
		
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("select b from "
						+ domainClass.getSimpleName()
						+ " b, "
						+ ConsumptionIdentifier.class.getSimpleName()
						+ " ci, " + Company.class.getSimpleName() + " c " + whereClause);
		query.setParameter("companyId", companyId);
		
		if(null != consumptionIdentifierId){
			query.setParameter("consumptionIdentifierId", consumptionIdentifierId);
		}
		
		if(null != startOpenTimestamp){
			query.setParameter("startOpenTimestamp", startOpenTimestamp);
		}
		if(null != endOpenTimestamp){
			query.setParameter("endOpenTimestamp", endOpenTimestamp);
		}
		if(null != startCloseTimestamp){
			query.setParameter("startCloseTimestamp", startCloseTimestamp);
		}
		if(null != endCloseTimestamp){
			query.setParameter("endCloseTimestamp", endCloseTimestamp);
		}
		
		if(null != statusList && !statusList.isEmpty()){			
			Iterator<String> iterator = statusList.iterator();
			
			for(int i = 0; iterator.hasNext(); i++){
				String status = iterator.next();
				query.setParameter("status"+i, status);
			}
		}
		
		return query.list();
	}

}
