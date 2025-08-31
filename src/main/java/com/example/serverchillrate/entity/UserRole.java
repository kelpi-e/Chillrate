package com.example.serverchillrate.entity;

import com.example.serverchillrate.secutiry.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserRole {

    @Id
    Integer id;
    String name;
    public static UserRole createByEnumRole(Role role){
        return UserRole.builder().id(role.ordinal()).name(role.name()).build();
    }
}
