package com.ssafy.model.mapper.auth;

import com.ssafy.model.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AuthMapper {
    Optional<User> findByProviderId(@Param("providerId") String providerId);

//    @Insert("INSERT INTO users (code_id, name, nickname, email, is_deleted, created_at) " +
//            "VALUES (#{codeId}, #{name}, #{nickname}, #{email}, #{isDeleted}, #{createdAt})")
//    @Options(useGeneratedKeys = true, keyProperty = "userId")
//    int insertUser(User user);
//
//    void insertSocialAccount(@Param("userId") Long userId, @Param("providerId") String providerId);
//
}
