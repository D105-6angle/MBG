package com.ssafy.controller.mission.photo;

import com.ssafy.model.entity.User;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/missions/photo/{roomId}/{missionId}")
@RequiredArgsConstructor
@Tag(name = "팀 미션", description = "인증샷 미션 API")
public class PhotoMissionController {
    private final PhotoMissionService photoMissionService;
    private final AuthService authService;

    @Operation(summary = "사진 등록", description = "해당 미션에 대해 사진 파일을 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(@AuthenticationPrincipal String providerId, @ModelAttribute MissionRequest.PhotoUpload request) throws IOException  {
        User user = authService.findUser(providerId);
        PhotoUploadResponse response = photoMissionService.uploadPhoto(
                request.getRoomId(),
                request.getMissionId(),
                request.getGroupNo(),
                request.getPhoto(),
                user.getUserId()
        );

        return ResponseEntity.ok(response);
    }
}