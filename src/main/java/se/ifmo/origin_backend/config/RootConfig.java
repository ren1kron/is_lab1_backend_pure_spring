package se.ifmo.origin_backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "se.ifmo.origin_backend")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "se.ifmo.origin_backend.repo")
@AllArgsConstructor
@PropertySource("classpath:application.properties")
@PropertySource("classpath:secret.properties")
public class RootConfig implements WebMvcConfigurer {

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

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource ds) {
        var vendor = new EclipseLinkJpaVendorAdapter();
        vendor.setGenerateDdl(true);
        vendor.setShowSql(true);

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ds);
        em.setJpaVendorAdapter(vendor);
        em.setPackagesToScan("se.ifmo.origin_backend");

        Properties jpa = new Properties();
        jpa.put(PersistenceUnitProperties.WEAVING, env.getProperty("jpa.weaving", "false"));
        jpa.put(PersistenceUnitProperties.DDL_GENERATION, env
            .getProperty("jpa.ddl.generation", "create-or-extend-tables"));
        jpa.put(PersistenceUnitProperties.LOGGING_LEVEL, env
            .getProperty("jpa.logging.level", "FINE"));
        jpa.put(PersistenceUnitProperties.DDL_GENERATION_MODE, env
            .getProperty("jpa.ddl.generation_output_mode", "database"));
        jpa.put(PersistenceUnitProperties.VALIDATION_MODE, "CALLBACK");
        jpa.put(PersistenceUnitProperties.TARGET_DATABASE, "org.eclipse.persistence.platform.database.PostgreSQLPlatform");
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

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    // === Bean validation wiring ===
    @Bean
    public MessageSource messageSource() {
        var ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("classpath:messages");
        ms.setDefaultEncoding("UTF-8");
        return ms;
    }

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        var v = new LocalValidatorFactoryBean();
        v.setValidationMessageSource(messageSource);
        return v;
    }

    @Override
    public Validator getValidator() {
        return validator(messageSource());
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(
        LocalValidatorFactoryBean validator) {
        var p = new MethodValidationPostProcessor();
        p.setValidator(validator);
        return p;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(30_000);
    }

}
