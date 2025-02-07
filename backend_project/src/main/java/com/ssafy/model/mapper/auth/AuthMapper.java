package com.ssafy.model.mapper.auth;

import com.ssafy.controller.auth.AuthRequest;
import com.ssafy.model.entity.Social;
import com.ssafy.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;

@Mapper
public interface AuthMapper {
    Optional<User> findByProviderId(@Param("providerId") String providerId);

    /*
        - 사용자 등록 성공: userId 값
        - 사용자 등록 실패: null
        Mapper에서 파라미터가 2개 이상 -> @Param 어노테이션 사용
     */
//    int insertUser(@Param("codeId") String codeId, @Param("userData") AuthRequest.UserInfoData userData);

    int insertUser(AuthRequest.RegistrationUserData userInfo);

    int insertSocial(Social socialData);
}
