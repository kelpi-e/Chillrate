package com.example.serverchillrate.repository;

import com.example.serverchillrate.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/*
репозиторий пользователей используется в security
 */
@Repository
public interface UserRepository extends JpaRepository<UserApp, UUID> {
    //поиск пользователей по почте
    Optional<UserApp> findByEmail (String email);
    Optional<UserApp> findByAdminToken(UUID adminToken);
}
