package com.study.service.web.controller;

import com.study.service.user.domain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/hello")
    public ResponseEntity<String> helloUser() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/user/auth1")
    public ResponseEntity<String> userAuth1(HttpServletRequest request) {
        request.getHeaders(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(
                userService.getAllUsers()
                        .stream()
                        .map(s -> s.getUsername())
                        .collect(Collectors.joining(","))
        );
    }
}
