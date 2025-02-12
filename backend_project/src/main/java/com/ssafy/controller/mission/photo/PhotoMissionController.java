package com.ssafy.controller.mission.photo;

import com.ssafy.model.service.auth.AuthService;
import com.ssafy.model.service.mission.photo.PhotoMissionService;
import com.ssafy.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/missions/photo/{roomId}/{missionId}")
@RequiredArgsConstructor
@Tag(name = "팀 미션", description = "인증샷 미션 API")
public class PhotoMissionController {

    private final PhotoMissionService photoMissionService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @Operation(summary = "사진 등록",
            description = "해당 미션에 대해 사진 파일을 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long roomId,
            @PathVariable Long missionId,
            @RequestParam("groupNo") int groupNo,
            @Parameter(
                    description = "업로드할 사진 파일",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam("photo") MultipartFile photo,
            HttpServletRequest request) {

        // JWT 토큰으로 사용자(user) 식별
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("인증 토큰이 없습니다.");
        }
        String token = authHeader.substring(7);
        String providerId = jwtTokenProvider.getProviderId(token);
        var user = authService.findUser(providerId);
        Long userId = user.getUserId();

        // 파일 저장 및 Picture 엔티티 등록
        var response = photoMissionService.uploadPhoto(roomId, missionId, groupNo, photo, userId);

        return ResponseEntity.ok(response);
    }
}