package com.ssafy.model.service.room;

import com.ssafy.controller.room.group.GroupDetailResponse;
import com.ssafy.controller.room.group.GroupResponse;
import com.ssafy.controller.room.member.MemberResponse;
import com.ssafy.exception.room.GroupNotFoundException;
import com.ssafy.model.mapper.room.GroupMapper;
import com.ssafy.model.mapper.room.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final RoomMapper roomMapper;
    private final GroupMapper groupMapper;

    public List<GroupResponse> getAllGroups(long roomId) {
        var room = roomMapper.selectRoomById(roomId);
        if (room == null) {
            throw new GroupNotFoundException("존재하지 않는 방입니다.");
        }

        // room.numOfGroups 값에 따라 그룹 번호 생성
        int numOfGroups = room.getNumOfGroups();
        int totalMissions = groupMapper.countTotalMissions(roomId);

        List<GroupResponse> groupList = new ArrayList<>();
        for (int groupNo = 1; groupNo <= numOfGroups; groupNo++) {
            int memberCount = groupMapper.countMembers(roomId, groupNo);
            int completed = groupMapper.countCompletedMissions(roomId, groupNo);
            double progress = (totalMissions == 0) ? 0 : (double) completed / totalMissions;

            groupList.add(GroupResponse.builder()
                    .groupNo(groupNo)
                    .memberCount(memberCount)
                    .progress(progress)
                    .build());
        }

        return groupList;
    }

    public GroupDetailResponse getGroupDetail(long roomId, int groupNo) {
        var room = roomMapper.selectRoomById(roomId);
        if (room == null) {
            throw new GroupNotFoundException("존재하지 않는 방입니다.");
        }

        // 그룹 번호 유효 확인
        if (groupNo < 1 || groupNo > room.getNumOfGroups()) {
            throw new GroupNotFoundException("유효하지 않은 그룹 번호입니다.");
        }

        List<MemberResponse> memberResponses = groupMapper.selectMembersByRoomAndGroup(roomId, groupNo);
        List<GroupDetailResponse.MemberDto> members = memberResponses.stream()
                .map(m -> GroupDetailResponse.MemberDto.builder()
                        .userId(m.getUserId())
                        .name(m.getName())
                        .nickname(m.getNickname())
                        .email(m.getEmail())
                        .build())
                .collect(Collectors.toList());

        int totalMissions = groupMapper.countTotalMissions(roomId);
        int completed = groupMapper.countCompletedMissions(roomId, groupNo);
        double progress = (totalMissions == 0) ? 0 : (double) completed / totalMissions;

        return GroupDetailResponse.builder()
                .groupNo(groupNo)
                .members(members)
                .progress(progress)
                .build();
    }

    public void deleteMember(long roomId, int groupNo, long userId) {
        int affectedRows = groupMapper.deleteMember(roomId, groupNo, userId);
        if (affectedRows == 0) {
            throw new GroupNotFoundException("해당 방, 조, 유저 정보를 찾을 수 없습니다.");
        }
    }
}
