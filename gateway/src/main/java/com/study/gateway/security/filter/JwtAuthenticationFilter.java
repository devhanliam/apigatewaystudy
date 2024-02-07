package com.study.gateway.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.gateway.security.jwt.JwtService;
import com.study.gateway.security.jwt.authentication.JwtAuthenticationToken;
import com.study.gateway.security.login.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    @Qualifier("jwtAuthenticationManager")
    private final ReactiveAuthenticationManager authenticationManager;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (checkHeader(exchange)) {


            String token = exchange
                    .getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION)
                    .substring(JwtService.BEARER.length());
            return authenticationManager.authenticate(JwtAuthenticationToken.unAuthenticated(token, ""))
                    .flatMap(auth ->
                        chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                    );
        }
        return chain.filter(exchange);
    }

    private boolean checkHeader(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return false;
        }
        return true;
    }

}
