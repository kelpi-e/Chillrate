package com.example.serverchillrate.config;

import com.example.serverchillrate.entity.UserRole;
import com.example.serverchillrate.repository.UserRoleRepository;
import com.example.serverchillrate.secutiry.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.sql.DataSource;
import java.util.List;

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
        //url при работы приложения из докера
        String url=((String)System.getenv("SPRING_DATASOURCE_URL"));
        //не найдено url для докера
        if(url==null){
            //url при работе локально
            url="jdbc:postgresql://localhost:5432/chillrate_db";
        }
        dataSourceBuilder.url(url);
        dataSourceBuilder.username((String)System.getenv("SPRING_DATASOURCE_USERNAME"));
        dataSourceBuilder.password((String)System.getenv("SPRING_DATASOURCE_PASSWORD"));
        return dataSourceBuilder.build();
    }


}
