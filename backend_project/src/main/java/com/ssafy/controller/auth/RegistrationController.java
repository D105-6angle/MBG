package com.ssafy.controller.auth;

import com.ssafy.model.service.auth.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
@Tag(name = "회원가입", description = "회원가입 API")
public class RegistrationController {
    private final RegistrationService registrationService;

//    @Operation(
//            summary = "사용자 회원가입",
//            description = "소셜 로그인 정보를 바탕으로 신규 사용자를 등록합니다."
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(example = "{ \"status\":400, \"message\":\"유효한 요청이 아닙니다.\" }")))
//    })
//    @PostMapping
//    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
////        RegisterResponse response = registrationService.register(registerRequest);
//        return ResponseEntity.ok(response);
//    }
}