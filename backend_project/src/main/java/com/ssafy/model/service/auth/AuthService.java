package com.ssafy.model.service.auth;
import com.ssafy.controller.auth.AuthRequest;
import com.ssafy.controller.auth.AuthResponse;
import com.ssafy.exception.auth.DuplicateUserException;
import com.ssafy.exception.auth.InvalidInputException;
import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.model.entity.Social;
import com.ssafy.model.entity.User;
import com.ssafy.model.mapper.auth.AuthMapper;
import com.ssafy.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMapper authMapper;
    private final JWTUtil jwtUtil;

    public AuthResponse.SuccessDto login(String providerId) {
        /*
            findByProviderId()가 Optional<User>를 반환
            만약 회원이 존재하면 User 객체가 반환됨
            회원이 존재하지 않으면 NotFoundUserException이 발생
         */
        // 회원 조회
        User user = authMapper.findByProviderId(providerId)
                .orElseThrow(() -> new NotFoundUserException("회원 정보가 없습니다."));  // 사용자 조회 실패 시

        // 조회 성공 시
        String accessToken = jwtUtil.createAccessToken(providerId);
        String refreshToken = jwtUtil.createRefreshToken(providerId);
        return AuthResponse.SuccessDto.builder().userId(user.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(user.getNickname())
                .message("로그인 성공")
                .build();
    }

    @Transactional
    public AuthResponse.SuccessDto regist(AuthRequest.UserInfoData userInfo, String typeCode) {
        String prodiverId = userInfo.getProviderId();;

        // 1. 중복 체크
        if (authMapper.findByProviderId(prodiverId).isPresent()) {
            throw new DuplicateUserException("이미 가입한 회원입니다.");
        }

        // 2. 사용자 정보 DB 저장
        String email = userInfo.getEmail();
        String name = userInfo.getName();
        String nickname = userInfo.getNickname();
        AuthRequest.RegistrationUserData registrationUserData = AuthRequest.RegistrationUserData.builder().codeId(typeCode)
                .providerId(prodiverId).email(email).name(name).nickname(nickname).build();

        int result = authMapper.insertUser(registrationUserData);
        if (result == 0) {
            throw new InvalidInputException("사용자 저장에 실패했습니다. 누락된 정보가 있어요.");
        }

        // 3. Social 정보 및 JWT 토큰 저장
        Long userId = registrationUserData.getUserId();
        String accessToken = jwtUtil.createAccessToken(prodiverId);
        String refreshToken = jwtUtil.createRefreshToken(prodiverId);
        Social socialData = Social.builder().userId(userId).codeId(typeCode).providerId(prodiverId).accessToken(accessToken)
                .refreshToken(refreshToken).build();

        result = authMapper.insertSocial(socialData);
        if (result == 0) {
            throw new InvalidInputException("사용자 저장에 실패했습니다. 누락된 정보가 있어요.");
        }

        // 4. 회원가입 성공 응답
        return AuthResponse.SuccessDto.builder().userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(userInfo.getNickname())
                .message("회원가입 성공! 바로 메인 페이지로 이동 부탁합니다.")
                .build();
    }
}