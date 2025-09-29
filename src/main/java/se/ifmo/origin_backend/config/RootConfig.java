package se.ifmo.origin_backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "se.ifmo.origin_backend")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "se.ifmo.origin_backend.repo")

@AllArgsConstructor
@PropertySource("classpath:application.properties")
public class RootConfig {

    private final Environment env;

    @Bean
    public DataSource dataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(env.getProperty("db.url"));
        cfg.setUsername(env.getProperty("db.username"));
        cfg.setPassword(env.getProperty("db.password"));
        cfg.setDriverClassName("org.postgresql.Driver");
        cfg.setMaximumPoolSize(env.getProperty("db.pool.max", Integer.class, 5));
        return new HikariDataSource(cfg);

    }

//    @Bean
//    public DataSource dataSource() {
//        JndiDataSourceLookup lookup = new JndiDataSourceLookup();
//        lookup.setResourceRef(true);
//        return lookup.getDataSource("jdbc/AppDS");
//    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource ds) {
        var vendor = new EclipseLinkJpaVendorAdapter();
        vendor.setGenerateDdl(true);
        vendor.setShowSql(true);

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ds);
        em.setJpaVendorAdapter(vendor);
        em.setPackagesToScan("se.ifmo.origin_backend.model");

        Properties jpa = new Properties();
        jpa.put(PersistenceUnitProperties.WEAVING,
                env.getProperty("jpa.weaving", "false"));
        jpa.put(PersistenceUnitProperties.DDL_GENERATION,
                env.getProperty("jpa.ddl.generation", "create-or-extend-tables"));
        jpa.put(PersistenceUnitProperties.LOGGING_LEVEL,
                env.getProperty("jpa.logging.level", "FINE"));
        em.setJpaProperties(jpa);

        return em;
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    // Only needed if you use @Value("${...}") anywhere in the app.
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
