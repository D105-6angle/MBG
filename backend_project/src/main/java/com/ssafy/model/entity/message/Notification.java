package com.ssafy.model.entity.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Notification {
    private String title;
    private String body;
    private String image;
}