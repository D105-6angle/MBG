package com.ssafy.model.mapper.auth;

import com.ssafy.controller.auth.AuthRequest;
import com.ssafy.model.entity.Social;
import com.ssafy.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {

    User findByUserId(Long userId);
    User findByProviderId(@Param("providerId") String providerId);
    int insertUser(AuthRequest.Registration userInfo);
    int insertSocial(Social socialData);
    Social findSocialByProviderId(@Param("providerId") String providerId);
    int updateRefreshToken(Social social);

    int updateUserForReJoin(@Param("userInfo") AuthRequest.Registration userInfo, @Param("userId") Long userId);        // 사용자 테이블 재가입
    int updateSocialForReJoin(@Param("providerId") String providerId, @Param("refreshToken") String refreshToken);      // 소셜 테이블 재가입
}
