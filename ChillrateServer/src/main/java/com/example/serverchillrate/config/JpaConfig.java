package com.example.serverchillrate.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JpaConfig {
    @Bean
    public DataSource dataSource(){
        DataSourceBuilder<?> dataSourceBuilder= DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url("jdbc:postgresql://"+(String)System.getenv("DATABASE_URL"));
        dataSourceBuilder.username((String)System.getenv("DATABASE_USERNAME"));
        dataSourceBuilder.password((String)System.getenv("DATABASE_PASSWORD"));
        return dataSourceBuilder.build();

    }
}
