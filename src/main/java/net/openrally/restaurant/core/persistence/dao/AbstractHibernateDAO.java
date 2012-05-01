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
	public T get(KeyType id) {
		Session session = sessionFactory.getCurrentSession();
		return (T) session.get(domainClass, id);
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
	
	public void refresh(T t){
		Session session = sessionFactory.getCurrentSession();
		session.refresh(t);
	}

	@SuppressWarnings("unchecked")
	public List<T> getList() {
		Session session = sessionFactory.getCurrentSession();
		Query queryResult = session.createQuery("from "
				+ domainClass.getSimpleName());
	    return queryResult.list();
	}

	public void deleteById(KeyType id) {
		Session session = sessionFactory.getCurrentSession();
		Object obj = get(id);
		session.delete(obj);
	}
	
	public void rollback(){
		Session session = sessionFactory.getCurrentSession();
		session.getTransaction().rollback();
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
		throw new RuntimeException("Unimplemented method deleteAll on AbstractHibernateDAO");
	}

	public int count() {
//		List list = hibernateTemplate.find(
//				"select count(*) from " + domainClass.getName() + " x");
//		Integer count = (Integer) list.get(0);
//		return count.intValue();
		throw new RuntimeException("Unimplemented method deleteAll on AbstractHibernateDAO");
	}
	
	public void flush(){
		Session session = sessionFactory.getCurrentSession();
		session.flush();
	}

}
