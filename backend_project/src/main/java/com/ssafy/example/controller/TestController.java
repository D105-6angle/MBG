package com.ssafy.example.controller;

import com.ssafy.example.mapper.UserMapper;
import com.ssafy.example.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    private final UserMapper userMapper;

    public TestController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userMapper.getAllUsers();
    }
}
