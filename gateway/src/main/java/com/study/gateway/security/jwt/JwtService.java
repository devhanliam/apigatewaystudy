package com.study.gateway.security.jwt;

import com.study.gateway.security.exceptionhandler.exception.CustomAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtService {

    public final static String BEARER = "Bearer ";
    private final SecretKey key;
    private final RedisTemplate<String,String> redisTemplate;

    public JwtService( @Value("${spring.jwt.secret}") String secretKey, RedisTemplate redisTemplate) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisTemplate = redisTemplate;
    }

    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    public String createAccessToken(Authentication authentication){
        Claims claims = Jwts.claims()
                .subject("access_token")
                .add("roles", authentication
                        .getAuthorities()
                        .stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.joining(",")))
                .build();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Authentication authentication){
        Claims claims = Jwts.claims()
                .subject("refresh_token")
                .add("roles", authentication
                        .getAuthorities()
                        .stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.joining(",")))
                .build();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);
        String refreshToken = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(key)
                .compact();

        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refreshExpirationTime,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    public Collection<? extends GrantedAuthority> getRoles(Authentication authentication) {
        Claims claims = parseClaims((String) authentication.getPrincipal());
        String roles = (String) claims.get("roles");
        return Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public boolean validateToken(String token) {
        try {
            Jwt<Header, Claims> claims = (Jwt<Header, Claims>) Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parse(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new CustomAuthenticationException("유효하지않는 토큰");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            //TODO: 토큰갱신 구현
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new CustomAuthenticationException("지원하지않는 토큰");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new CustomAuthenticationException("유효하지않는 토큰");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            Jwt<Header, Claims> claims = (Jwt<Header, Claims>) Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parse(accessToken);
            return claims.getPayload();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

//    private String doTokenRefreshing(Claims claims) {
//        UsernamePasswordAuthenticationToken.authenticated(claims.get)
//
//    }







}
