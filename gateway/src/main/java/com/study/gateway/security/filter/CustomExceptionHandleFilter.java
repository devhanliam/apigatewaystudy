package com.study.gateway.security.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CustomExceptionHandleFilter implements WebFilter {
    private final ServerAuthenticationEntryPoint authenticationEntryPoint;
    private final ServerAccessDeniedHandler accessDeniedHandler;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(AuthenticationException.class ,(authenticationException)-> authenticationEntryPoint.commence(exchange, authenticationException));
    }
}
