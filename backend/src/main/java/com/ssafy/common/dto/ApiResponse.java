package com.ssafy.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T>{
    private int status;
    private String message;
    private T data;
}
