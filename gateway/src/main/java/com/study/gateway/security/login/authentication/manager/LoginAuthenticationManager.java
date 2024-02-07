package com.study.gateway.security.login.authentication.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginAuthenticationManager implements ReactiveAuthenticationManager {
    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        return userDetailsService.findByUsername(username)
                .filter(userDetails -> passwordEncoder.matches(password, userDetails.getPassword()))
                .switchIfEmpty(Mono.defer(()-> Mono.error(new BadCredentialsException("잘못된 유저"))))
                .flatMap(userDetails -> Mono.defer(()->Mono.just(UsernamePasswordAuthenticationToken.authenticated(userDetails.getUsername(),userDetails.getPassword(),userDetails.getAuthorities()))));
    }
}
