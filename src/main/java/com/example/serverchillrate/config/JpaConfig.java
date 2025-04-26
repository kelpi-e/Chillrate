package com.example.serverchillrate.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JpaConfig {
/*
Конфигурация подключения к базе данных
Данные из переменных окружения
 */
    @Bean
    public DataSource dataSource(){
        DataSourceBuilder<?> dataSourceBuilder= DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url((String)System.getenv("SPRING_DATASOURCE_URL"));
        dataSourceBuilder.username((String)System.getenv("SPRING_DATASOURCE_USERNAME"));
        dataSourceBuilder.password((String)System.getenv("SPRING_DATASOURCE_PASSWORD"));
        return dataSourceBuilder.build();

    }
}
