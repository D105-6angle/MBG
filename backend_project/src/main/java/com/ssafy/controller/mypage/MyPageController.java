package com.ssafy.controller.mypage;


import com.ssafy.controller.auth.AuthRequest;
import com.ssafy.controller.common.FailResponse;
import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.exception.auth.WithdrawnUserException;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.service.amazons3.S3Service;
import com.ssafy.model.service.mypage.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지 API")
public class MyPageController {
    private final MyPageService myPageService;
    private final S3Service s3Service;

    @Operation(summary = "닉네임 변경")
    @PatchMapping("{userId}/nickname")
    public ResponseEntity<?> changeNickname(@PathVariable Long userId, @RequestBody MyPageRequest.ChangedNickname data, @AuthenticationPrincipal String providerId) {
        String newNickname = data.getNickname();
        myPageService.changeNickname(providerId, userId, newNickname);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("{userId}")
    public ResponseEntity<?> withdrawUser(@AuthenticationPrincipal String providerId, @PathVariable Long userId) {
        myPageService.withdrawUser(providerId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "마이페이지 정보 조회")
    @GetMapping("{userId}")
    public ResponseEntity<?> getMyPageInformations(@AuthenticationPrincipal String providerId, @PathVariable Long userId,
                                                   @RequestParam(value = "roomId", required = false) Long roomId) {
        MyPageResponse.MyPageInfo response = myPageService.getMyPageInfo(providerId, userId, roomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "풀이기록 상세 조회")
    @GetMapping("/{userId}/card/{cardId}")
    public ResponseEntity<?> getLogDetail(@PathVariable Long userId, @PathVariable Long cardId) {
        MyPageResponse.LogDetail response = myPageService.getLogDetail(userId, cardId);
        return ResponseEntity.ok(response);
    }
}
