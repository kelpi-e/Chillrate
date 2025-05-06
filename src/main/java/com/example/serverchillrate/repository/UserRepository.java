package com.example.serverchillrate.repository;

import com.example.serverchillrate.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/*
репозиторий пользователей используется в security
 */
@Repository
public interface UserRepository extends JpaRepository<UserApp,Long> {
    //поиск пользователей по почте
    Optional<UserApp> findByEmail (String email);
}
