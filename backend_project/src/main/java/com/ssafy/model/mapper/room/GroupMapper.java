package com.ssafy.model.mapper.room;

import com.ssafy.controller.room.group.GroupDetailResponse.MemberDto;
import com.ssafy.controller.room.member.MemberResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface GroupMapper {

    // 조의 수 조회
    int selectNumOfGroupsByRoomId(@Param("roomId") long roomId);

    //특정 방, 특정 조에 속한 멤버
    List<MemberResponse> selectMembersByRoomAndGroup(
            @Param("roomId") long roomId,
            @Param("groupNo") int groupNo
    );

    //특정 방, 특정 조에 속한 멤버
    int countMembers(
            @Param("roomId") long roomId,
            @Param("groupNo") int groupNo
    );

    // 전체 미션 수 (rooms -> tmissions 등)
    int countTotalMissions(@Param("roomId") long roomId);

    // 특정 조가 완료한 미션 수
    int countCompletedMissions(
            @Param("roomId") long roomId,
            @Param("groupNo") int groupNo
    );

    // memberships 테이블에서 멤버 삭제
    int deleteMember(
            @Param("roomId") long roomId,
            @Param("groupNo") int groupNo,
            @Param("userId") long userId
    );
}