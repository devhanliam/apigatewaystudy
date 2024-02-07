package com.study.gateway.security.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.gateway.security.filter.CustomExceptionHandleFilter;
import com.study.gateway.security.filter.JwtAuthenticationFilter;
import com.study.gateway.security.filter.LoginProcessingFilter;
import com.study.gateway.security.jwt.JwtService;
import com.study.gateway.security.jwt.authentication.manager.JwtAuthenticationManager;
import com.study.gateway.security.login.authentication.manager.LoginAuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ServerAuthenticationEntryPoint authenticationEntryPoint;
    private final ServerAccessDeniedHandler accessDeniedHandler;
    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(e -> e.pathMatchers("/user/hello","/login")
                .permitAll()
                .anyExchange().hasRole("USER"));

        http.csrf(c -> c.disable());
        http.httpBasic(h -> h.disable());
        http.formLogin(f -> f.disable());
        http.addFilterBefore(new CustomExceptionHandleFilter(authenticationEntryPoint, accessDeniedHandler), SecurityWebFiltersOrder.AUTHENTICATION);
        http.addFilterAt(new LoginProcessingFilter(loginAuthenticationManger(),jwtService,objectMapper),SecurityWebFiltersOrder.AUTHENTICATION);
        http.addFilterAfter(new JwtAuthenticationFilter(jwtAuthenticationManager()), SecurityWebFiltersOrder.AUTHENTICATION);
        http.exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint));
        http.authenticationManager(jwtAuthenticationManager());
        http.authenticationManager(loginAuthenticationManger());
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return http.build();
    }


    @Bean
    public JwtAuthenticationManager jwtAuthenticationManager() {
        return new JwtAuthenticationManager(jwtService);
    }

    @Primary
    @Bean
    public LoginAuthenticationManager loginAuthenticationManger() {
        return new LoginAuthenticationManager(userDetailsService, passwordEncoder());
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
