package com.ssafy.model.mapper.room;

import com.ssafy.controller.room.member.MemberResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {

    // 특정 유저 조회
    MemberResponse findMemberById(@Param("userId") long userId);

}