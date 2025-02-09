package com.ssafy.controller.mypage;


import com.ssafy.controller.auth.AuthRequest;
import com.ssafy.controller.common.FailResponse;
import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.service.mypage.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지 API")
public class MyPageController {
    private final MyPageService myPageService;

    @Operation(summary = "닉네임 변경")
    @PatchMapping("{userId}/nickname")
    public ResponseEntity<?> changeNickname(@PathVariable Long userId, @RequestBody MyPageRequest.ChangedNickname data) {
        try {
            String newNickname = data.getNickname();
            myPageService.changeNickname(userId, newNickname);
            return ResponseEntity.ok().build();
        } catch (NotFoundUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FailResponse.builder().status(404)
                    .message("사용자를 찾을 수 없습니다.").error("404 Not Found").build());
        } catch (DatabaseOperationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FailResponse.builder().status(500)
                    .message("닉네임 변경에 실패했습니다.").error("Internal Server Error").build());
        }
    }

//    @Operation(summary = "회원 탈퇴")
//    @DeleteMapping("{userId}")
//    public ResponseEntity<?> withdrawUser(@PathVariable Long userId) {
//        try {
//            myPageService.withdrawUser(userId);
//            return ResponseEntity.ok().build();
//        } catch (No)
//    }
}
