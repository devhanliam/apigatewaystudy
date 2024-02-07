package com.study.service.user.domain.service.impl;

import com.study.service.user.domain.entity.User;
import com.study.service.user.domain.service.UserService;
import com.study.service.user.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
