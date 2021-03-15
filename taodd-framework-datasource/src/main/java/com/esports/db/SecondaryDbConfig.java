package com.esports.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        entityManagerFactoryRef = "entityManagerFactorySecondary",
        transactionManagerRef="transactionManagerSecondary",
        basePackages = {"com.esports.**.db2"}
)
public class SecondaryDbConfig {

    @Autowired(required = false)
    private JpaProperties jpaProperties;

    @Bean(name = "entityManagerFactorySecondary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary(
            EntityManagerFactoryBuilder builder, @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        return builder
                .dataSource(secondaryDataSource)
                .packages("com.esports.**.bean.db2")
                .persistenceUnit("secondaryPersistenceUnit")
                .properties(jpaProperties.getProperties())
                .build();
    }

    @Bean(name = "entityManagerSecondary")
    public EntityManager entityManager(
            EntityManagerFactoryBuilder builder,@Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        return entityManagerFactorySecondary(builder, secondaryDataSource).getObject().createEntityManager();
    }

    @Bean(name = "transactionManagerSecondary")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactorySecondary") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "secondaryJdbcTemplate")
    public JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondaryDataSource") DataSource SecondaryDataSource) {
        return new JdbcTemplate(SecondaryDataSource);
    }
}
