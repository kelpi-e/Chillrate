package com.example.serverchillrate.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Builder
@Data
public class AuthUser implements UserDetails {
    UUID uuid;
    Collection<?extends  GrantedAuthority> authorities;
    @Builder.Default
    boolean confirmMail=false;
    UUID secret;
    @Builder.Default
    String device=null;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }
}
