package com.ssafy.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private int id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
