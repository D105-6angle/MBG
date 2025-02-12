package com.ssafy.controller.room.group;

import com.ssafy.model.service.auth.AuthService;
import com.ssafy.model.service.room.GroupService;
import com.ssafy.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms/{roomId}/groups/select")
@RequiredArgsConstructor
@Tag(name = "조", description = "조 선택 API")
public class GroupSelectController {

    private final GroupService groupService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @Operation(summary = "조 선택 및 소속 저장")
    @PostMapping
    public ResponseEntity<?> selectGroup(@PathVariable long roomId,
                                         @RequestBody GroupSelectRequest request,
                                         HttpServletRequest httpRequest) {
        // 토큰에서 학생 userId 추출
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("인증 토큰이 없습니다.");
        }
        String token = authHeader.substring(7);
        String providerId = jwtTokenProvider.getProviderId(token);
        var user = authService.findUser(providerId);

        // 조 선택 후 Membership 생성
        var membership = groupService.joinGroup(roomId, request.getGroupNo(), user.getUserId());

        GroupSelectResponse response = GroupSelectResponse.builder()
                .userId(membership.getUserId())
                .roomId(membership.getRoomId())
                .groupNo(membership.getGroupNo())
                .codeId(membership.getCodeId())
                .build();

        return ResponseEntity.ok(response);
    }

}
