package com.ssafy.model.service.auth;
import com.ssafy.controller.auth.LoginResponse;
import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.model.entity.User;
import com.ssafy.model.mapper.auth.AuthMapper;
import com.ssafy.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMapper authMapper;
    private final JWTUtil jwtUtil;

    public LoginResponse.SuccessDto login(String providerId) {
        /*
            findByProviderId()가 Optional<User>를 반환
            만약 회원이 존재하면 User 객체가 반환됨
            회원이 존재하지 않으면 NotFoundUserException이 발생
         */
        // providerId로 회원 조회
        User user = authMapper.findByProviderId(providerId)
                .orElseThrow(() -> new NotFoundUserException("회원 정보가 없습니다."));  // 사용자 조회 실패 시

        // 조회 성공 시
        String accessToken = jwtUtil.createAccessToken(providerId);
        String refreshToken = jwtUtil.createRefreshToken(providerId);
        return LoginResponse.SuccessDto.builder().userId(user.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(user.getNickname())
                .message("로그인 성공")
                .build();
    }
}