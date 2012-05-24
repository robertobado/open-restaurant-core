package net.openrally.restaurant.core.persistence.dao;

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

}
