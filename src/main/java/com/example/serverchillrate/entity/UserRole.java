package com.example.serverchillrate.entity;

import com.example.serverchillrate.secutiry.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole {

    @Id
    Role id;
    @Enumerated(value = EnumType.STRING)
    Role name;
}
