package com.ssafy.model.mapper.mypage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MypageMapper {
    int changeNickname(@Param("userId") Long userId, @Param("newNickname") String newNickname);

}
