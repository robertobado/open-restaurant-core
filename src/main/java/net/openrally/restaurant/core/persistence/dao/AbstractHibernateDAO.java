package net.openrally.restaurant.core.persistence.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class AbstractHibernateDAO<T extends Serializable, KeyType extends Serializable> {

	protected SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Class<T> domainClass = getDomainClass();

	/**
	 * Method to return the class of the domain object
	 */
	protected abstract Class<T> getDomainClass();

	@SuppressWarnings("unchecked")
	public T load(KeyType id) {
		Session session = sessionFactory.getCurrentSession();
		return (T) session.load(domainClass, id);
	}

	public void update(T t) {
		Session session = sessionFactory.getCurrentSession();
		session.update(t);
	}

	public void save(T t) {
		Session session = sessionFactory.getCurrentSession();
		session.save(t);
	}

	public void delete(T t) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(t);
	}

	@SuppressWarnings("unchecked")
	public List<T> getList() {
		Session session = sessionFactory.getCurrentSession();
		Query queryResult = session.createQuery("from "
				+ domainClass.getName());
	    return queryResult.list();
	}

	public void deleteById(KeyType id) {
		Session session = sessionFactory.getCurrentSession();
		Object obj = load(id);
		session.delete(obj);
	}

	public void deleteAll() {
//		Session session = sessionFactory.getCurrentSession();
//		session.execute(new HibernateCallback() {
//			public Object doInHibernate(Session session)
//					throws HibernateException {
//				String hqlDelete = "delete " + domainClass.getName();
//				int deletedEntities = session.createQuery(hqlDelete)
//						.executeUpdate();
//				return null;
//			}
//
//		});
	}

	public int count() {
//		List list = hibernateTemplate.find(
//				"select count(*) from " + domainClass.getName() + " x");
//		Integer count = (Integer) list.get(0);
//		return count.intValue();
		return -1;
	}

}
