package com.example.serverchillrate.repository;

import com.example.serverchillrate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/*
репозиторий пользователей используется в security
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    //поиск пользователей по имени
    Optional<User> findByUsername(String username);
}
