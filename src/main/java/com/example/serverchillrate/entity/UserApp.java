package com.example.serverchillrate.entity;

import com.example.serverchillrate.secutiry.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/*
класс пользователь
используется одновременно как entity для базы данных
и как userDetails для security
*/
@Table(name = "_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserApp implements UserDetails {
    @Id
    UUID id;
    @Column(unique = true)
    private String email;
    private String name;
    private String  password;
    @Enumerated(EnumType.STRING)
    Role role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
