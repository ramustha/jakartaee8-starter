package com.example.it;

import com.example.GreetingEntity;
import com.example.GreetingRepository;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Arquillian.class)
public class GreetingRepositoryTest {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(GreetingEntity.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("jbossas-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    GreetingRepository repository;

    @Test
    @UsingDataSet("greeting.yml")
    public void fetchAll() {
        List<GreetingEntity> all = repository.findAll();
        assertThat(all, Matchers.hasSize(1));

        GreetingEntity greetingEntity = all.get(0);
        Assert.assertEquals("Hello JakartaEE", greetingEntity.getMessage());
    }

    @Test
    @UsingDataSet("greeting.yml")
    public void findById() {
        GreetingEntity greetingEntity = repository.findById(2L);
        Assert.assertEquals(1L, greetingEntity.getId().longValue());
        Assert.assertEquals("Hello JakartaEE 2", greetingEntity.getMessage());
    }
}
