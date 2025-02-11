package com.ssafy.model.service.room;

import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.controller.room.group.GroupDetailResponse;
import com.ssafy.exception.room.GroupNotFoundException;
import com.ssafy.model.entity.Membership;
import com.ssafy.model.entity.Room;
import com.ssafy.model.mapper.room.GroupMapper;
import com.ssafy.model.mapper.room.RoomMapper;
import lombok.RequiredArgsConstructor;
import com.ssafy.controller.room.group.GroupResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final RoomMapper roomMapper;
    private final GroupMapper groupMapper;

    // 전체 조 목록 조회
    public List<GroupResponse> getAllGroups(long roomId, int numOfGroups) {
        List<GroupResponse> result = new ArrayList<>();
        for (int groupNo = 1; groupNo <= numOfGroups; groupNo++) {
            // groupMapper.countMembers(...) 사용
            int count = groupMapper.countMembers(roomId, groupNo);

            // GroupResponse: groupNo와 memberCount만 담는 DTO
            GroupResponse resp = GroupResponse.builder()
                    .groupNo(groupNo)
                    .memberCount(count)
                    .build();

            result.add(resp);
        }
        return result;
    }

    // 단일 조 상세 조회
    public GroupDetailResponse getGroupDetail(long roomId, int groupNo) {
        // 방 유효성 확인
        Room room = roomMapper.selectRoomById(roomId);
        if (room == null) {
            throw new GroupNotFoundException("존재하지 않는 방 (roomId=" + roomId + ")");
        }
        if (groupNo < 1 || groupNo > room.getNumOfGroups()) {
            throw new GroupNotFoundException("유효하지 않은 groupNo=" + groupNo);
        }

        // 진행률
        int totalMissions = groupMapper.countTotalMissions();
        int completed = groupMapper.countCompletedMissions(roomId, groupNo);
        double progress = (totalMissions == 0) ? 0.0 : (completed / (double) totalMissions) * 100.0;

        // 멤버 목록
        var memberDataList = groupMapper.selectMembers(roomId, groupNo);
        List<GroupDetailResponse.MemberDto> members = new ArrayList<>();
        for (var md : memberDataList) {
            GroupDetailResponse.MemberDto dto = GroupDetailResponse.MemberDto.builder()
                    .userId(md.getUserId())
                    .nickname(md.getNickname())
                    .codeId(md.getCodeId())   // "J001"=팀장, "J002"=팀원
                    .build();
            members.add(dto);
        }

        // 인증샷 목록
        var photoDataList = groupMapper.selectVerificationPhotos(roomId, groupNo);
        List<GroupDetailResponse.VerificationPhotoDto> verificationPhotos = new ArrayList<>();
        for (var pd : photoDataList) {
            GroupDetailResponse.VerificationPhotoDto dto = GroupDetailResponse.VerificationPhotoDto.builder()
                    .pictureId(pd.getPictureId())
                    .pictureUrl(pd.getPictureUrl())
                    .missionId(pd.getMissionId())
                    .completionTime(pd.getCompletionTime())
                    .build();
            verificationPhotos.add(dto);
        }

        // 방문한 장소 목록
        var placeDataList = groupMapper.selectVisitedPlaces(roomId, groupNo);
        List<GroupDetailResponse.VisitedPlaceDto> visitedPlaces = new ArrayList<>();
        for (var vd : placeDataList) {
            GroupDetailResponse.VisitedPlaceDto dto = GroupDetailResponse.VisitedPlaceDto.builder()
                    .missionId(vd.getMissionId())
                    .positionName(vd.getPositionName())
                    .completedAt(vd.getCompletedAt())
                    .build();
            visitedPlaces.add(dto);
        }

        return GroupDetailResponse.builder()
                .groupNo(groupNo)
                .progress(progress)
                .members(members)
                .verificationPhotos(verificationPhotos)
                .visitedPlaces(visitedPlaces)
                .build();
    }

    // 특정 조원 삭제
    public void deleteMember(long roomId, int groupNo, long userId) {
        int rows = groupMapper.deleteMember(roomId, groupNo, userId);
        if (rows == 0) {
            throw new GroupNotFoundException("해당 조원 정보가 존재하지 않습니다.");
        }
    }

    // 학생의 조 선택 및 소속 저장
    public Membership joinGroup(long roomId, int groupNo, long userId) {
        // 기본 코드: "J002" (팀원)
        Membership membership = Membership.builder()
                .userId(userId)
                .roomId(roomId)
                .groupNo(groupNo)
                .codeId("J002")
                .build();
        int rows = groupMapper.insertMembership(membership);
        if (rows == 0) {
            throw new DatabaseOperationException("조 선택에 실패했습니다.");
        }
        return membership;
    }



    // 사진 제출 후 조장 재배정
//    public void reassignLeader(long roomId, int groupNo, long userId) {
//        Long currentLeader = groupMapper.findLeaderInGroup(roomId, groupNo);
//        if (currentLeader == null) {
//            return;
//        }
//        if (!currentLeader.equals(userId)) {
//            return;
//        }
//        groupMapper.updateMemberToMember(roomId, groupNo, userId);
//        Long newLeader = groupMapper.findNewLeaderCandidate(roomId, groupNo, userId);
//        if (newLeader != null) {
//            groupMapper.updateMemberToLeader(roomId, groupNo, newLeader);
//        }
//    }

    // joinGroup(long roomId, int groupNo, long userId)

}
