package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.exception.InternalServerErrorException;
import net.openrally.restaurant.core.persistence.entity.User;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("userDAO")
@Transactional
public class UserDAO extends AbstractHibernateDAO<User, Long>{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	protected Class<User> getDomainClass() {
		return User.class;
	}
	
	@SuppressWarnings("unchecked")
	public User loadByCompanyIdAndLogin(long companyId, String login) {
		
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Querying users for companyId = [%d] and login [%s]", companyId, login));
		}
		
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from " + getDomainClass().getSimpleName()
				+ " where companyId = :companyId and login = :login");
		query.setParameter("companyId", companyId);
		query.setParameter("login", login);
		List<User> list = query.list();
		
		if(list == null || list.size() == 0){
			return null;
		}
		else if (list.size() == 1){
			return list.get(0);
		}
		else{
			logger.error(String.format("Multiple entries found while querying users for companyId = [%d] and login [%s]", companyId, login));
			throw new InternalServerErrorException();
		}
	}
}
