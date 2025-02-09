package com.ssafy.model.service.mypage;

import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.entity.User;
import com.ssafy.model.mapper.auth.AuthMapper;
import com.ssafy.model.mapper.mypage.MypageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final AuthMapper authMapper;
    private final MypageMapper mypageMapper;

    // TODO: 수정 필요
    @Transactional
    public void changeNickname(Long userId, String newNickname) {
        // 1. userId에 해당하는 사용자가 존재하는지 확인
        User user = authMapper.findByUserId(userId);
        if (user == null) {
            throw new NotFoundUserException("사용자를 찾을 수 없습니다.");
        }

        //TODO: 본인인지 확인 필요

        // 2. 닉네임 변경 시도
        int result = mypageMapper.changeNickname(userId, newNickname);
        if (result == 0) {
            throw new DatabaseOperationException("닉네임 변경에 실패하였습니다.");
        }
    }

    @Transactional
    public void withdrawUser(Long userId) {
        // 1. userId에 해당하는 사용자가 존재하는지 확인
        User user = authMapper.findByUserId(userId);
        if (user == null) {
            throw new NotFoundUserException("사용자를 찾을 수 없습니다.");
        }

        // 2. 회원 탈퇴
        int result = mypageMapper.withdrawUser(userId);
        if (result == 0) {
            throw new DatabaseOperationException("회원 탈퇴에 실패하였습니다.");
        }
    }
}
