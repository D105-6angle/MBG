package com.ssafy.model.mapper.room;

import com.ssafy.controller.room.group.GroupDetailResponse.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupMapper {

    // 조에 속한 멤버 수
    int countMembers(@Param("roomId") Long roomId,
                     @Param("groupNo") int groupNo);

    // 전체 인증샷 미션의 총 개수
    int countTotalMissions();

    // 조가 완료한 인증샷 미션 수
    int countCompletedMissions(@Param("roomId") Long roomId,
                               @Param("groupNo") int groupNo);

    // 특정 조 멤버 목록
    List<MemberDto> selectMembers(@Param("roomId") Long roomId,
                                  @Param("groupNo") int groupNo);

    // 인증샷 목록 조회
    List<VerificationPhotoDto> selectVerificationPhotos(@Param("roomId") Long roomId,
                                                        @Param("groupNo") int groupNo);

    // 방문한 장소 목록 조회
    List<VisitedPlaceDto> selectVisitedPlaces(@Param("roomId") Long roomId,
                                              @Param("groupNo") int groupNo);

    // 특정 조원 삭제
    int deleteMember(@Param("roomId") long roomId,
                     @Param("groupNo") int groupNo,
                     @Param("userId") long userId);

    // 조 선택 시 Membership 생성
    int insertMembership(@Param("membership") com.ssafy.model.entity.Membership membership);


}
