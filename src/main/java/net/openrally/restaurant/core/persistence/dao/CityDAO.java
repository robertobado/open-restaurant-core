package net.openrally.restaurant.core.persistence.dao;

import net.openrally.restaurant.core.persistence.entity.City;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



@Repository("cityDAO")
@Transactional
public class CityDAO extends AbstractHibernateDAO<City,Integer>{

    @Override
    protected Class<City> getDomainClass() {
        return City.class;
    }
}
