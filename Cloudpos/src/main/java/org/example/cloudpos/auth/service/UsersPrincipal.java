package org.example.cloudpos.auth.service;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Collection;
import java.util.Map;


@Getter
public class UsersPrincipal implements OAuth2User {

    private final String userId;
    private final String name;

    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public UsersPrincipal(String userId,
                          String name,
                          Collection<? extends GrantedAuthority> authorities,
                          Map<String, Object> attributes) {
        this.userId = userId;
        this.name = name;
        this.authorities = authorities;
        this.attributes = attributes;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}