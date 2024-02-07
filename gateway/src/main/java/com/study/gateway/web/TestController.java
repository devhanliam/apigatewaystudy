package com.study.gateway.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @PostMapping("/login")
    public Mono<String> login() {

        return Mono.just("로그인 완료");
    }
}
