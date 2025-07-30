package com.example.serverchillrate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String name;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    UserApp admin;
    @ManyToMany(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    List<UserApp> clients;

    public List<UUID> clientsToListId(){
        return clients.stream().map(UserApp::getId).collect(Collectors.toList());
    }
}
