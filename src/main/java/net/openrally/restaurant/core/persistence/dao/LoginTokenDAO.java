package net.openrally.restaurant.core.persistence.dao;

import java.util.List;

import net.openrally.restaurant.core.exception.InternalServerErrorException;
import net.openrally.restaurant.core.persistence.entity.LoginToken;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("loginTokenDAO")
@Transactional
public class LoginTokenDAO extends AbstractHibernateDAO<LoginToken, Long>{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	protected Class<LoginToken> getDomainClass() {
		return LoginToken.class;
	}

	@SuppressWarnings("unchecked")
	public LoginToken loadByToken(String loginToken) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from " + getDomainClass().getSimpleName()
				+ " where token = :token ");
		query.setParameter("token", loginToken);
		List<LoginToken> list = query.list();
		
		if(list == null || list.size() == 0){
			return null;
		}
		else if (list.size() == 1){
			return list.get(0);
		}
		else{
			logger.error(String.format("Multiple entries found while querying login token for token = [%s] ", loginToken));
			throw new InternalServerErrorException();
		}
	}
	
}
