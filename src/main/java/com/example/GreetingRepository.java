package com.example;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class GreetingRepository {

    @PersistenceContext
    private EntityManager em;

    public List<GreetingEntity> fetchAll() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<GreetingEntity> query = criteriaBuilder.createQuery(GreetingEntity.class);
        Root<GreetingEntity> from = query.from(GreetingEntity.class);
        CriteriaQuery<GreetingEntity> select = query.select(from);

        return em.createQuery(select).getResultList();
    }

    public GreetingEntity getGreetingById(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<GreetingEntity> query = criteriaBuilder.createQuery(GreetingEntity.class);

        Root<GreetingEntity> fromGreeting = query.from(GreetingEntity.class);
        Predicate isEqualToGivenId = criteriaBuilder.equal(fromGreeting.get(com.example.GreetingEntity_.id), id);
        query.select(fromGreeting).where(isEqualToGivenId);

        return em.createQuery(query).getSingleResult();
    }
}
