package com.ssafy.model.service.auth;
import com.ssafy.controller.auth.AuthRequest;
import com.ssafy.controller.auth.AuthResponse;
import com.ssafy.exception.auth.DuplicateUserException;
import com.ssafy.exception.auth.InvalidInputException;
import com.ssafy.exception.auth.InvalidTokenException;
import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.entity.Social;
import com.ssafy.model.entity.User;
import com.ssafy.model.mapper.auth.AuthMapper;
import com.ssafy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMapper authMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse.SuccessDto login(String providerId) {
        // 1. 사용자 조회
        User user = authMapper.findByProviderId(providerId);
        if (user == null) {
            throw new NotFoundUserException("회원 정보가 없습니다.");  // 사용자 조회 실패 시
        }

        // 2. 조회 성공 시
        String accessToken = jwtTokenProvider.createAccessToken(providerId);
        String refreshToken = jwtTokenProvider.createRefreshToken(providerId);
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
        User user = authMapper.findByProviderId(prodiverId);

        // 1. 중복 체크
        if (user != null) {
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
        String accessToken = jwtTokenProvider.createAccessToken(prodiverId);
        String refreshToken = jwtTokenProvider.createRefreshToken(prodiverId);
        Social socialData = Social.builder().userId(userId).codeId(typeCode).providerId(prodiverId)
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

    @Transactional
    public AuthResponse.SuccessDto reissue(String refreshToken) {
        // 1. refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }

        // 2. refresh Token으로 사용자 정보 가져오기
        String providerId = jwtTokenProvider.getProviderId(refreshToken);
        // 3. DB에 저장된 refresh Token과 비교
        Social social = authMapper.findSocialByProviderId(providerId);
        if (social == null || !refreshToken.equals(social.getRefreshToken())) {
            throw new InvalidTokenException("저장된 토큰 정보가 일치하지 않습니다.");
        }

        // 4. 새로운 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(providerId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(providerId);

        // 5. DB의 refresh Token 업데이트
        social.updateRefreshToken(newRefreshToken);
        int result = authMapper.updateRefreshToken(social);
        if (result == 0) {
            throw new DatabaseOperationException("Refresh Token 업데이트에 실패했습니다.");
        }

        return AuthResponse.SuccessDto.builder().accessToken(newAccessToken).refreshToken(newRefreshToken)
                .message("토큰 재발급에 성공했습니다.").build();
    }
}