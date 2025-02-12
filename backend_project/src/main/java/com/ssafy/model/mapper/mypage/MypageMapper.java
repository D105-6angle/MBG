package com.ssafy.model.mapper.mypage;

import com.ssafy.controller.mypage.MyPageResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MypageMapper {
    int changeNickname(@Param("userId") Long userId, @Param("newNickname") String newNickname);
    int withdrawUser(Long userId);

    // 마이페이지 조회
    MyPageResponse.UserInfo getMyPageUserInfo(@Param("userId") Long userId);
    List<MyPageResponse.AttemptedProblem> getAttempedProblems(@Param("userId") Long userId);
}
