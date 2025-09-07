package com.example.serverchillrate.repository;

import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.entity.AuthorizationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorizationDetailsrepository extends JpaRepository<AuthorizationDetails,Long> {
    List<AuthorizationDetails> findAllByUserApp(UserApp userApp);
    Optional<AuthorizationDetails> findByUserAppAndDevice(UserApp userApp, String device);

}
