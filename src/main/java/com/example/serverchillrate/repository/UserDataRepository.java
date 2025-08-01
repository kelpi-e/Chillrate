package com.example.serverchillrate.repository;

import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
    List<UserData> findUserDataBy_user(UserApp _user);
}
