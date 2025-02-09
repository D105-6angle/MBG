package com.ssafy.model.service.mypage;

import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.exception.auth.WithdrawnUserException;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.entity.User;
import com.ssafy.model.mapper.auth.AuthMapper;
import com.ssafy.model.mapper.mypage.MypageMapper;
import com.ssafy.model.service.auth.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final AuthService authService;
//    private final AuthMapper authMapper;
    private final MypageMapper mypageMapper;

    @Transactional
    public void changeNickname(String providerId, Long userId, String newNickname) throws NotFoundUserException, DatabaseOperationException, WithdrawnUserException {
        User user = authService.findUser(providerId);                                   // 1. 사용자 찾기
        authService.validateUserWithdrawal(user);                                       // 2. 탈퇴한 유저인지 확인
        authService.validateResourceOwnership(user, userId);                            // 3. 본인인지 확인
        int result = mypageMapper.changeNickname(userId, newNickname);                  // 4. 닉네임 변경 시도
        authService.validateDatabaseOperation(result, "닉네임 변경");       // 5. DB에 잘 반영됐는지 확인
    }

    @Transactional
    public void withdrawUser(String providerId, Long userId) throws NotFoundUserException, DatabaseOperationException{
        User user = authService.findUser(providerId);                               // 1. 사용자 찾기
        authService.validateUserWithdrawal(user);                                   // 2. 탈퇴한 유저인지 확인
        authService.validateResourceOwnership(user, userId);                        // 3. 본인인지 확인
        int result = mypageMapper.withdrawUser(userId);                             // 4. 회원 탈퇴
        authService.validateDatabaseOperation(result, "회원 탈퇴");  // 5. DB에 잘 반영됐는지 확인
    }
}
