package com.ssafy.controller.room.group;

import com.ssafy.model.service.room.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms/{roomId}/groups")
@RequiredArgsConstructor
@Tag(name = "조", description = "조 API")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "단일 조 상세 조회")
    @GetMapping("/{groupNo}")
    public ResponseEntity<?> getGroupDetail(@PathVariable long roomId,
                                            @PathVariable int groupNo) {
        GroupDetailResponse detail = groupService.getGroupDetail(roomId, groupNo);
        return ResponseEntity.ok(detail);
    }

    @Operation(summary = "조원 삭제")
    @DeleteMapping("/{groupNo}/members/{userId}")
    public ResponseEntity<?> deleteMember(@PathVariable long roomId,
                                          @PathVariable int groupNo,
                                          @PathVariable long userId) {
        groupService.deleteMember(roomId, groupNo, userId);
        return ResponseEntity.ok().build();
    }
}
