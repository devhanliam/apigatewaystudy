package com.study.gateway.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.gateway.security.exceptionhandler.exception.CustomAuthenticationException;
import com.study.gateway.security.jwt.JwtService;
import com.study.gateway.security.jwt.TokenInfo;
import com.study.gateway.security.login.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class LoginProcessingFilter implements WebFilter {

    @Qualifier("loginAuthenticationManager")
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getPath().value().equals("/login")) {
            return ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders())
                    .body(BodyExtractors.toMono(LoginRequest.class))
                    .flatMap(body -> authenticationManager
                            .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(body.getUsername()
                                    , body.getPassword())))
                    .flatMap(auth -> {
                        ServerHttpResponse response = setResponse(exchange, auth);
                        return response.writeWith(Mono.defer(() -> {
                            try {
                                DataBuffer dataBuffer = response.bufferFactory()
                                        .wrap(objectMapper.writeValueAsBytes(auth.getName()));
                                return Mono.just(dataBuffer);
                            } catch (JsonProcessingException e) {
                                return Mono.error(new CustomAuthenticationException("데이터 파싱 중 에러"));
                            }
                        }));

                    });
        }
            return chain.filter(exchange);


    }

    private ServerHttpResponse setResponse(ServerWebExchange exchange, Authentication auth) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String accessToken = JwtService.BEARER + jwtService.createAccessToken(auth);
        String refreshToken = JwtService.BEARER + jwtService.createRefreshToken(auth);
        response.getHeaders().set(HttpHeaders.AUTHORIZATION, accessToken);
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken.substring(JwtService.BEARER.length()))
                .path("/").build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken.substring(JwtService.BEARER.length()))
                .path("/").build();
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return response;
    }
}