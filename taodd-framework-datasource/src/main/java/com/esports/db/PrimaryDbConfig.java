package com.esports.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryPrimary",
        transactionManagerRef="transactionManagerPrimary",
        basePackages = {"com.esports.**.db1"}
)
public class PrimaryDbConfig {

    @Autowired(required = false)
    private JpaProperties jpaProperties;

    @Primary
    @Bean(name = "entityManagerFactoryPrimary")
    @Qualifier("entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(
            EntityManagerFactoryBuilder builder, @Qualifier("primaryDataSource") DataSource primaryDataSource) {
        return builder
                .dataSource(primaryDataSource)
                .packages("com.esports.**.bean.db1")
                .persistenceUnit("primaryPersistenceUnit")
                .properties(jpaProperties.getProperties())
                .build();
    }

    @Primary
    @Bean(name = "entityManagerPrimary")
    public EntityManager entityManager(
            EntityManagerFactoryBuilder builder,@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        return entityManagerFactoryPrimary(builder, primaryDataSource).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactoryPrimary") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean(name = "primaryJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }
}
