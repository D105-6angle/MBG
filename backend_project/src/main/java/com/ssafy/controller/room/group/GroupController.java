package com.ssafy.controller.room.group;

import com.ssafy.controller.room.group.GroupDetailResponse;
import com.ssafy.controller.room.group.GroupResponse;
import com.ssafy.model.service.room.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/rooms/{roomId}/groups")
@RequiredArgsConstructor
@Tag(name = "조", description = "조 API")
public class GroupController {

    private final GroupService groupService;
    @Operation(summary = "특정 방의 전체 조를 조회")
    @GetMapping
    public ResponseEntity<?> getAllGroups(@PathVariable long roomId) {
        List<GroupResponse> groups = groupService.getAllGroups(roomId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", groups);
        return ResponseEntity.ok(responseBody);
    }
    @Operation(summary = "특정 방의 특정 조를 조회")
    @GetMapping("/{groupNo}")
    public ResponseEntity<?> getGroupDetail(@PathVariable long roomId,
                                            @PathVariable int groupNo) {
        GroupDetailResponse detail = groupService.getGroupDetail(roomId, groupNo);
        return ResponseEntity.ok(Map.of("data", detail));
    }

    @Operation(summary = "특정 방, 특정 조의 특정 조원을 삭제")
    @DeleteMapping("/{groupNo}/members/{userId}")
    public ResponseEntity<?> deleteMember(@PathVariable long roomId,
                                          @PathVariable int groupNo,
                                          @PathVariable long userId) {
        groupService.deleteMember(roomId, groupNo, userId);
        return ResponseEntity.ok("조원 삭제 완료");
    }
}


