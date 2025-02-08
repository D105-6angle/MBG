package com.ssafy.model.mapper.auth;

import com.ssafy.controller.auth.AuthRequest;
import com.ssafy.model.entity.Social;
import com.ssafy.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;

@Mapper
public interface AuthMapper {
    User findByProviderId(@Param("providerId") String providerId);
    int insertUser(AuthRequest.RegistrationUserData userInfo);
    int insertSocial(Social socialData);
}
