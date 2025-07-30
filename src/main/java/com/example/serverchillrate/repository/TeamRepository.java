package com.example.serverchillrate.repository;

import com.example.serverchillrate.entity.Team;
import com.example.serverchillrate.entity.UserApp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
    @Fetch(FetchMode.JOIN)
    public List<Team> findAllByAdmin(UserApp admin);
}
