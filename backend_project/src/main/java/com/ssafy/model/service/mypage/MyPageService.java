package com.ssafy.model.service.mypage;

import com.ssafy.controller.mypage.MyPageResponse;
import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.exception.auth.WithdrawnUserException;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.exception.mypage.NotFoundCardException;
import com.ssafy.model.entity.Card;
import com.ssafy.model.entity.Report;
import com.ssafy.model.entity.User;
import com.ssafy.model.mapper.auth.AuthMapper;
import com.ssafy.model.mapper.mypage.MypageMapper;
import com.ssafy.model.mapper.report.ReportMapper;
import com.ssafy.model.service.amazons3.S3Service;
import com.ssafy.model.service.auth.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final AuthService authService;
    private final MypageMapper mypageMapper;
    private final ReportMapper reportMapper;
    private final S3Service s3Service;

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

    public MyPageResponse.MyPageInfo getMyPageInfo(String providerId, Long userId, Long roomId) throws NotFoundUserException, DatabaseOperationException {
        User user = authService.findUser(providerId);           // 1. 사용자 찾기
        authService.validateUserWithdrawal(user);               // 2. 탈퇴한 유저인지 확인
        authService.validateResourceOwnership(user, userId);    // 3. 본인 확인

        // 4. 만족도 조사를 다 했는지 확인
        Report report = null;
        boolean isFinishedReport = false;
        if (roomId != null) {
            report = reportMapper.getReport(roomId, userId);
            isFinishedReport = (report != null);
        }

        MyPageResponse.UserInfo userInfo = mypageMapper.getMyPageUserInfo(userId);                      // 유저 정보 가져오기
        userInfo.setFinishedReport(isFinishedReport);

        List<MyPageResponse.AttemptedProblem> problems = mypageMapper.getAttempedProblems(userId);      // 시도한 카드에 대한 설명 가져오기
        for (MyPageResponse.AttemptedProblem problem : problems) {
            // imageUrl이 null이 아닐 때만 presignedUrl 생성
            if (problem.getImageUrl() != null && !problem.getImageUrl().trim().isEmpty()) {
                String presignedUrl = s3Service.generatePresignedUrl(problem.getImageUrl());
                problem.setImageUrl(presignedUrl);
            }
        }

        MyPageResponse.MyPageInfo response = MyPageResponse.MyPageInfo.builder().userInfo(userInfo).attemptedProblems(problems).build();
        return response;
    }

    // 풀이기록 상세 조회
    public MyPageResponse.LogDetail getLogDetail(Long userId, Long cardId) throws NotFoundCardException {
        Card card = mypageMapper.getCard(cardId);
        if (card == null) {
            throw new NotFoundCardException("카드를 찾을 수 없습니다.");
        }

        MyPageResponse.LogDetail response = mypageMapper.getLogDetail(userId, cardId);
        String presignedUrl = s3Service.generatePresignedUrl(response.getImageUrl());
        response.setImageUrl(presignedUrl);

        if (response == null) {
            throw new NotFoundCardException("풀이기록에 없는 카드입니다.");
        }
        return response;
    }
}
