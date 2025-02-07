package com.ssafy.model.service.auth;

import com.ssafy.controller.auth.RegisterRequest;
import com.ssafy.controller.auth.RegisterResponse;
import com.ssafy.model.entity.User;
import com.ssafy.model.mapper.auth.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final AuthMapper authMapper;

//    public RegisterResponse register(RegisterRequest registerRequest) {
//        if (registerRequest.getProviderId() == null || registerRequest.getProviderId().isBlank()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "providerId는 필수 값입니다.");
//        }
//
//        if (authMapper.findByProviderId(registerRequest.getProviderId()).isPresent()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 사용자입니다.");
//        }
//
//        User newUser = User.builder()
//                .codeId("U001")  // 기본적으로 학생 계정으로 등록
//                .name(registerRequest.getName())
//                .nickname(registerRequest.getNickname())
//                .email(registerRequest.getEmail())
//                .isDeleted(false)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        // users 테이블에 삽입
//        authMapper.insertUser(newUser);
//        Long userId = newUser.getUserId();
//
//        if (userId == null) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 등록 중 오류 발생");
//        }
//
//        // social_accounts 테이블에 삽입
//        authMapper.insertSocialAccount(userId, registerRequest.getProviderId());
//
//        return RegisterResponse.fromUser(newUser);
//    }


}