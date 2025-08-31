package com.example.serverchillrate.repository;

import com.example.serverchillrate.entity.UserApp;
import com.example.serverchillrate.entity.UserSecurityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSecurityDetailsRepository extends JpaRepository<UserSecurityDetails,Long> {
    List<UserSecurityDetails> findAllByUserApp(UserApp userApp);
    Optional<UserSecurityDetails> findByUserAppAndDevice(UserApp userApp,String device);

}
