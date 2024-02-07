package com.study.gateway.security.jwt.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;
    private Object credentials;

    public JwtAuthenticationToken(String token, Object credentials) {
        super(null);
        this.token = token;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(String token, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    public static JwtAuthenticationToken authenticated(String token, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        return new JwtAuthenticationToken(token, credentials, authorities);
    }

    public static JwtAuthenticationToken unAuthenticated(String token, Object credentials) {
        return new JwtAuthenticationToken(token, credentials);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.token;
    }

}
