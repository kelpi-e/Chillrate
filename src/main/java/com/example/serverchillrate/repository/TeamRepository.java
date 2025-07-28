package com.example.serverchillrate.repository;

import com.example.serverchillrate.models.Team;
import com.example.serverchillrate.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
    public List<Team> findAllByAdmin(UserApp admin);
}
