package com.example.it;

import com.example.GreetingEntity;
import com.example.GreetingRepository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(Arquillian.class)
public class GreetingPersistenceTest {
    private static final Logger LOG = LoggerFactory.getLogger(GreetingPersistenceTest.class);

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(GreetingEntity.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("jbossas-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @PersistenceContext
    EntityManager em;

    @Inject
    GreetingRepository repository;

    @Resource
    ManagedExecutorService executor;

    @Test
    @InSequence(1)
    @Transactional(TransactionMode.ROLLBACK)
    public void shouldPersistsGreeting() throws Exception {
        // given
        GreetingEntity greetingEntity = new GreetingEntity(1L, "Welcome");

        // when
        em.persist(greetingEntity);
        em.flush();
        em.clear();

        @SuppressWarnings("unchecked")
        List<GreetingEntity> entities = em.createQuery(selectAllInJPQL(GreetingEntity.class)).getResultList();

        // then
        assertThat(entities, hasSize(1));
    }

    @Test
    @InSequence(2)
    @Transactional(TransactionMode.ROLLBACK)
    public void shouldNotPersistsGreeting() throws Exception {
        // given
        GreetingEntity greetingEntity = new GreetingEntity(1L, "Welcome");

        // when
        em.persist(greetingEntity);
        em.clear(); // when call clear persist must flush first to saved into database

        @SuppressWarnings("unchecked")
        List<GreetingEntity> entities = em.createQuery(selectAllInJPQL(GreetingEntity.class)).getResultList();

        // then
        assertThat(entities, hasSize(0));
    }

    @Test
    @InSequence(3)
    @Transactional(TransactionMode.ROLLBACK)
    public void shouldLastUpdateWin() throws Exception {
        GreetingEntity greetingEntity = new GreetingEntity(1L, "Welcome");
        em.persist(greetingEntity);

        GreetingEntity findGreeting = em.find(GreetingEntity.class, 1L);
        findGreeting.setMessage("update greeting");
        LOG.info("trying to update greeting {} with {}", findGreeting.getId(), findGreeting.getMessage());

        GreetingEntity findAnotherGreeting = em.find(GreetingEntity.class, 1L);
        findAnotherGreeting.setMessage("update greeting again");
        LOG.info("another user trying to update greeting {} with {}", findGreeting.getId(), findGreeting.getMessage());

        em.merge(findGreeting);
        em.merge(findAnotherGreeting);

        GreetingEntity find = em.find(GreetingEntity.class, 1L);

        // then
        assertThat(find.getMessage(), equalTo("update greeting again"));
    }

    @Test
    @InSequence(4)
    @UsingDataSet("greeting.yml")
    @Transactional(TransactionMode.ROLLBACK)
    public void shouldFirstUpdateWin() throws Exception {
        assertThat(repository.findAll(), notNullValue());
        assertThat(repository.findAll().isEmpty(), equalTo(false));

        CountDownLatch countDownLatch = new CountDownLatch(2);

        // first update
        executor.execute(() -> {
            GreetingEntity findGreeting = repository.findById(1L, LockModeType.OPTIMISTIC);
            LOG.info("trying to update {} ", findGreeting);

            findGreeting.setMessage("Hello World");
            repository.merge(findGreeting);

            countDownLatch.countDown();
        });

        // second update
        executor.execute(() -> {
            GreetingEntity findGreeting = repository.findById(1L, LockModeType.OPTIMISTIC);
            LOG.info("another user trying to update {} ", findGreeting);

            // fake loading
            sleepOneSeconds();

            try {
                findGreeting.setMessage("update greeting");
                repository.merge(findGreeting);
            } catch (RuntimeException e) {
                LOG.error("Row was updated or deleted by another transaction");
            }

            countDownLatch.countDown();
        });

        countDownLatch.await();
    }

    private void sleepOneSeconds() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String selectAllInJPQL(Class<?> clazz) {
        return "SELECT entity FROM " + clazz.getSimpleName() + " entity";
    }
}
