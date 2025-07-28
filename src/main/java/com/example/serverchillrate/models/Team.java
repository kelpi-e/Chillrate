package com.example.serverchillrate.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.event.ListDataEvent;
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
    Long id;
    String name;
    @ManyToOne
    UserApp admin;
    @ManyToMany
    List<UserApp> clients;

    public List<UUID> clientsToListId(){
        return clients.stream().map(UserApp::getId).collect(Collectors.toList());
    }
}
