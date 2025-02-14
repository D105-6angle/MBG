package com.ssafy.model.service.room;

import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.controller.room.group.GroupDetailResponse;
import com.ssafy.exception.room.GroupNotFoundException;
import com.ssafy.model.entity.Membership;
import com.ssafy.model.entity.Room;
import com.ssafy.model.mapper.room.GroupMapper;
import com.ssafy.model.mapper.room.RoomMapper;
import com.ssafy.model.service.amazons3.S3Service;
import lombok.RequiredArgsConstructor;
import com.ssafy.controller.room.group.GroupResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final RoomMapper roomMapper;
    private final GroupMapper groupMapper;
    private final S3Service s3Service;

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

            if (pd.getPictureUrl() != null) {
                String presignedUrl = s3Service.generatePresignedUrl(pd.getPictureUrl());
                dto.setPictureUrl(presignedUrl);
            }

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

    // 현재 조에서 조장(J001) 찾기
    public Long findLeaderInGroup(long roomId, int groupNo) {
        return groupMapper.findLeaderInGroup(roomId, groupNo);
    }


    // 학생의 조 선택 및 소속 저장
    @Transactional
    public Membership joinGroup(long roomId, int groupNo, long userId) {

        // 현재 그룹에 조장이 이미 존재하는지 확인
        Optional<Long> currentLeader = Optional.ofNullable(findLeaderInGroup(roomId, groupNo));

        String newCodeId = currentLeader.isPresent() ? "J002" : "J001";

        Membership membership = Membership.builder()
                .userId(userId)
                .roomId(roomId)
                .groupNo(groupNo)
                .codeId(newCodeId)
                .build();

        int rows = groupMapper.insertMembership(membership);
        if (rows == 0) {
            throw new DatabaseOperationException("조 선택에 실패했습니다.");
        }
        return membership;
    }

}
