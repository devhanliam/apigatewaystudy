package com.study.gateway.security.login.authentication.manager;

import com.study.gateway.database.repository.UserRepository;
import com.study.gateway.security.login.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
//@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginUserDetailService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("없는 회원"))))
                .flatMap(user -> Mono.defer(() ->Mono.just(new LoginUser(user.getUsername(), user.getPassword(), user.getRolesStringToGrant()))));
    }
}
