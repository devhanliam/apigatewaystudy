package com.study.gateway.security.jwt.authentication.manager;

import com.study.gateway.security.exceptionhandler.exception.CustomAuthenticationException;
import com.study.gateway.security.jwt.JwtService;
import com.study.gateway.security.jwt.authentication.JwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
       return Mono.just(authentication)
                .filter(this::isSupport)
                .filter(auth -> jwtService.validateToken((String) auth.getPrincipal()))
                .switchIfEmpty(Mono.error(() -> new CustomAuthenticationException("지원하지 않는 인증")))
                .flatMap(auth -> Mono.just(JwtAuthenticationToken.authenticated((String) auth.getPrincipal(),"",jwtService.getRoles(auth))));
    }

    public boolean isSupport(Authentication authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication.getClass());
    }
}
