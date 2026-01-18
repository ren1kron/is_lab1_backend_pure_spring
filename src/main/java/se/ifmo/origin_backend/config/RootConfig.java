package se.ifmo.origin_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
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

        log.info("JdbcUrl: {}", cfg.getJdbcUrl());
        log.info("db.username: {}", cfg.getUsername());
        log.info("db.password: {}", cfg.getPassword());
        log.info("db.driverClassName: {}", cfg.getDriverClassName());

        return new HikariDataSource(cfg);
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource ds) {
        var vendor = new HibernateJpaVendorAdapter();
        vendor.setGenerateDdl(true);
        vendor.setShowSql(env.getProperty("hibernate.show_sql", Boolean.class, false));
        vendor.setDatabasePlatform(env.getProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));

        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ds);
        em.setJpaVendorAdapter(vendor);
        em.setEntityManagerFactoryInterface(EntityManagerFactory.class);
        em.setPackagesToScan("se.ifmo.origin_backend");

        Properties jpa = new Properties();
        jpa.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto", "update"));
        jpa.put("hibernate.show_sql", env.getProperty("hibernate.show_sql", "false"));
        jpa.put("hibernate.format_sql", env.getProperty("hibernate.format_sql", "false"));
        jpa.put("hibernate.dialect", env.getProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
        jpa.put("jakarta.persistence.validation.mode",
            env.getProperty("jakarta.persistence.validation.mode", "CALLBACK"));
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

    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // registry.addResourceHandler("/**")
    // .addResourceLocations( "classpath:/static/",
    // "classpath:/public/",
    // "classpath:/META-INF/resources/",
    // "/" // for src/main/webapp
    // )
    // .setCachePeriod(7 * 24 * 60 * 60) .resourceChain(true);
    // }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
