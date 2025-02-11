package com.ssafy.model.mapper.room;

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
    List<MemberData> selectMembers(@Param("roomId") Long roomId,
                                   @Param("groupNo") int groupNo);

    // 인증샷 목록 조회
    List<VerificationPhotoData> selectVerificationPhotos(@Param("roomId") Long roomId,
                                                         @Param("groupNo") int groupNo);

    // 방문한 장소 목록 조회
    List<VisitedPlaceData> selectVisitedPlaces(@Param("roomId") Long roomId,
                                               @Param("groupNo") int groupNo);

    // 특정 조원 삭제
    int deleteMember(@Param("roomId") long roomId,
                     @Param("groupNo") int groupNo,
                     @Param("userId") long userId);

    // 리더 찾기 / 새 리더 후보 찾기 / 코드 업데이트
    Long findLeaderInGroup(@Param("roomId") Long roomId,
                           @Param("groupNo") int groupNo);

    Long findNewLeaderCandidate(@Param("roomId") Long roomId,
                                @Param("groupNo") int groupNo,
                                @Param("excludeUserId") Long excludeUserId);

    int updateMemberToLeader(@Param("roomId") Long roomId,
                             @Param("groupNo") int groupNo,
                             @Param("userId") Long userId);

    int updateMemberToMember(@Param("roomId") Long roomId,
                             @Param("groupNo") int groupNo,
                             @Param("userId") Long userId);


    // 내부 DTO 클래스들
    public static class MemberData {
        public long userId;
        public String nickname;
        public String codeId; // ex) "J001"=팀장
    }

    public static class VerificationPhotoData {
        public long pictureId;
        public String pictureUrl;
        public long missionId;
        public String completionTime;
    }

    public static class VisitedPlaceData {
        public long missionId;
        public String positionName;
        public String completedAt;
    }
}
