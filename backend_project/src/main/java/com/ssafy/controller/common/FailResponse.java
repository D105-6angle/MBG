package com.ssafy.controller.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FailResponse {
    private int status;
    private String message;
    private String error;
}
