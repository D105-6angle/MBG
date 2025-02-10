package com.ssafy.model.service.auth;
import com.ssafy.controller.auth.AuthRequest;
import com.ssafy.controller.auth.AuthResponse;
import com.ssafy.exception.auth.*;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.entity.Social;
import com.ssafy.model.entity.User;
import com.ssafy.model.mapper.auth.AuthMapper;
import com.ssafy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthMapper authMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse.SuccessDto login(String providerId) throws NotFoundUserException, WithdrawnUserException {
        log.debug("로그인 프로세스 시작 - Provider ID: {}", providerId);

        // 1. 사용자 조회
        User user = findUser(providerId);
        log.debug("사용자 조회 완료 - User ID: {}", user.getUserId());

        // 2. 탈퇴한 사용자인지 확인
        validateUserWithdrawal(user);
        // 3. 조회 성공 시
        String accessToken = jwtTokenProvider.createAccessToken(providerId);
        String refreshToken = jwtTokenProvider.createRefreshToken(providerId);
        log.info("토큰 발급 완료 - Provider ID: {}, User ID: {}", providerId, user.getUserId());

        return AuthResponse.SuccessDto.builder().userId(user.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(user.getNickname())
                .message("로그인 성공")
                .build();
    }

    @Transactional
    public AuthResponse.SuccessDto regist(AuthRequest.UserInfo userInfo, String typeCode) throws DatabaseOperationException, DuplicateUserException {
        String providerId = userInfo.getProviderId();
        User user = authMapper.findByProviderId(providerId);
        String email, name, nickname;

        // 1. 이미 가입한 적이 있는 경우
        if (user != null) {
            // 회원 탈퇴하지 않은 경우
            if (!user.isDeleted()) {
                throw new DuplicateUserException("이미 가입한 회원입니다.");
            }

            // 회원 탈퇴 한 경우 (재가입)
            email = userInfo.getEmail();
            name = userInfo.getName();
            nickname = userInfo.getNickname();
            AuthRequest.Registration data = AuthRequest.Registration.builder().codeId(typeCode).providerId(providerId).email(email).name(name).nickname(nickname).build();

            // 사용자 정보 업데이트
            int result = authMapper.updateUserForReJoin(data, user.getUserId());
            validateDatabaseOperation(result, "사용자 정보 저장");

            // 소셜 정보 업데이트
            String accessToken = jwtTokenProvider.createAccessToken(providerId);
            String refreshToken = jwtTokenProvider.createRefreshToken(providerId);
            result = authMapper.updateSocialForReJoin(providerId, refreshToken);
            validateDatabaseOperation(result, "소셜 정보 저장");

            return AuthResponse.SuccessDto.builder().userId(user.getUserId())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .nickname(userInfo.getNickname())
                    .message("회원가입 성공! 바로 메인 페이지로 이동 부탁합니다.")
                    .build();

        } else {    // 2. 신규 가입인 경우
        // 사용자 정보 DB 저장
        email = userInfo.getEmail();
        name = userInfo.getName();
        nickname = userInfo.getNickname();
        AuthRequest.Registration data = AuthRequest.Registration.builder().codeId(typeCode)
                .providerId(providerId).email(email).name(name).nickname(nickname).build();

        int result = authMapper.insertUser(data);
        validateDatabaseOperation(result, "사용자 정보 저장");

        // 3. Social 정보 및 JWT 토큰 저장
        Long userId = data.getUserId();
        String accessToken = jwtTokenProvider.createAccessToken(providerId);
        String refreshToken = jwtTokenProvider.createRefreshToken(providerId);
        Social socialData = Social.builder().userId(userId).codeId(typeCode).providerId(providerId)
                .refreshToken(refreshToken).build();

        result = authMapper.insertSocial(socialData);
        validateDatabaseOperation(result, "소셜 정보 저장");

        // 4. 회원가입 성공 응답
        return AuthResponse.SuccessDto.builder().userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(userInfo.getNickname())
                .message("회원가입 성공! 바로 메인 페이지로 이동 부탁합니다.")
                .build();
        }
    }

    @Transactional
    public AuthResponse.SuccessDto reissue(String providerId, String refreshToken) throws InvalidTokenException, DatabaseOperationException {
        // 1. refresh Token 유효성 검증
        validateToken(refreshToken);
        // 2. DB에 저장된 refresh Token과 비교
        Social social = findSocial(providerId);
        validateEqualToken(social.getRefreshToken(), refreshToken);

        // 3. 새로운 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(providerId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(providerId);
        // 4. DB의 refresh Token 업데이트
        social.updateRefreshToken(newRefreshToken);
        int result = authMapper.updateRefreshToken(social);
        validateDatabaseOperation(result, "Refresh Token 업데이트");

        return AuthResponse.SuccessDto.builder().accessToken(newAccessToken).refreshToken(newRefreshToken)
                .message("토큰 재발급에 성공했습니다.").build();
    }

    /* 사용자 조회 및 존재 여부 확인 */
    public User findUser(String providerId) throws NotFoundUserException {
        log.debug("사용자 조회 시도 - Provider ID: {}", providerId);

        User user = authMapper.findByProviderId(providerId);
        if (user == null) {
            log.warn("미가입 사용자 접근 - Provider ID: {}", providerId);
            throw new NotFoundUserException("사용자를 찾을 수 없습니다.");
        }

        return user;
    }

    public Social findSocial(String providerId) throws NotFoundUserException {
        Social social = authMapper.findSocialByProviderId(providerId);
        if (social == null) {
            throw new NotFoundUserException("소셜 정보를 찾을 수 없습니다.");
        }
        return social;
    }

    /* 사용자 탈퇴 여부 확인 */
    public void validateUserWithdrawal(User user) throws WithdrawnUserException {
        if (user.isDeleted()) {
            throw new WithdrawnUserException("탈퇴한 사용자입니다.");
        }
    }

    /* 리소스 접근 권한 확인 */
    public void validateResourceOwnership(User user, Long userId) throws UnauthorizedException {
        if (!user.getUserId().equals(userId)) {
            throw new UnauthorizedException("본인이 아닌 리소스에 대한 접근 권한이 없습니다.");
        }
    }

    /* 데이터베이스 작업 결과 검증 */
    public void validateDatabaseOperation(int result, String operationName) throws DatabaseOperationException {
        if (result == 0) {
            throw new DatabaseOperationException(operationName + " 작업이 실패했습니다.");
        }
    }

    /* DB에 저장된 토큰과 일치하는지 검사 */
    public void validateEqualToken(String dbToken, String inputToken) throws InvalidTokenException {
        if (!dbToken.equals(inputToken)) {
            throw new InvalidTokenException("저장된 토큰과 일치하지 않습니다.");
        }
    }

    /* 토큰 유효성 검사 */
    public void validateToken(String token) throws InvalidTokenException {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }
}