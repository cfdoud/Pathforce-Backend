package com.pathdx.repository;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Transactional
public abstract class BaseRepoService<T, ID extends Serializable> {

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Gets repository.
     *
     * @return the repository
     */
    protected abstract JpaRepository<T, ID> getRepository();

    /**
     * Gets entity class.
     *
     * @return the entity class
     */
    protected abstract Class<T> getEntityClass();

    /**
     * Gets session.
     *
     * @return the session
     */
    protected Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    @Bean
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public <S extends T> S save(S entity) {
        return getRepository().save(entity);
    }

    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<S>();

        for (S entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    /**
     * Gets criteria.
     *
     * @return the criteria
     */
    protected Criteria getCriteria() {
        return getSession().createCriteria(getEntityClass());
    }

    public void addPagingAndSorting(Criteria criteria, int max, int offset, Order order) {
        criteria.setFirstResult(offset)
                .setMaxResults(max)
                .addOrder(order);
    }

}
