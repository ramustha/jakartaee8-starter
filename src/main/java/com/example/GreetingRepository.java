package com.example;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class GreetingRepository {

    @PersistenceContext
    private EntityManager em;

    public List<GreetingEntity> findAll() {
        return em.createNamedQuery("GreetingEntity.findAll", GreetingEntity.class).getResultList();
    }

    public GreetingEntity findById(Object id) {
        return em.find(GreetingEntity.class, id);
    }

    public GreetingEntity findById(Object id, LockModeType lockModeType) {
        return em.find(GreetingEntity.class, id, lockModeType);
    }

    public void merge(GreetingEntity entity) {
        em.merge(entity);
        em.flush();
    }

    public void persist(GreetingEntity entity) {
        em.persist(entity);
        em.flush();
    }

    public EntityTransaction getTransaction() {
        return em.getTransaction();
    }
}
