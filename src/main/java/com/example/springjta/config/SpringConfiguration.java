package com.example.springjta.config;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SpringConfiguration {

    private final XADataSourceWrapper wrapper;

    @Autowired
    public SpringConfiguration(XADataSourceWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Bean
    @ConfigurationProperties(prefix = "a")
    public DataSource a() throws Exception {
        return wrapper.wrapDataSource(dataSource("a"));
    }

    @Bean
    @ConfigurationProperties(prefix = "b")
    public DataSource b() throws Exception {
        return wrapper.wrapDataSource(dataSource("b"));
    }

    @Bean
    public DataSourceInitializer aInit(DataSource a) {
        return init(a, "a");
    }

    @Bean
    public DataSourceInitializer bInit(DataSource b) {
        return init(b, "b");
    }

    private DataSourceInitializer init(DataSource dataSource, String name) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(
                new ResourceDatabasePopulator(
                        new ClassPathResource(name + ".sql")
                )
        );
        return dataSourceInitializer;
    }

    private JdbcDataSource dataSource(String fileName) {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL("jdbc:h2:./" + fileName);
        jdbcDataSource.setUser("sa");
        jdbcDataSource.setPassword("");
        return jdbcDataSource;
    }
}
