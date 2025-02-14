package com.ssafy.model.service.mission.heritage;

import com.ssafy.controller.mission.heritage.HeritageMissionAnswerRequest;
import com.ssafy.controller.mission.heritage.HeritageMissionAnswerResponse;
import com.ssafy.controller.mission.heritage.HeritageMissionResponse;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.exception.common.InvalidRequestException;
import com.ssafy.exception.mission.MissionNotFoundException;
import com.ssafy.model.entity.HeritageProblem;
import com.ssafy.model.entity.Log;
import com.ssafy.model.mapper.mission.heritage.HeritageMissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeritageMissionService {

    private final HeritageMissionMapper heritageMissionMapper;

    @Transactional(readOnly = true)
    public HeritageMissionResponse getHeritageMissionByMissionId(Long missionId) {
        if (missionId == null) {
            throw new InvalidRequestException("미션 ID는 필수값입니다.");
        }

        try {
            HeritageProblem quiz = heritageMissionMapper.findByMissionId(missionId)
                    .orElseThrow(() -> new MissionNotFoundException("미션 ID: " + missionId + "에 해당하는 퀴즈를 찾을 수 없습니다."));

            if (quiz.getExample1() == null || quiz.getExample2() == null ||
                    quiz.getExample3() == null || quiz.getExample4() == null ||
                    quiz.getAnswer() == null) {
                throw new InvalidRequestException("퀴즈 데이터가 완전하지 않습니다.");
            }

            List<String> choices = new ArrayList<>(Arrays.asList(
                    quiz.getExample1(),
                    quiz.getExample2(),
                    quiz.getExample3(),
                    quiz.getExample4()
            ));
            Collections.shuffle(choices);

            return new HeritageMissionResponse(
                    quiz.getProblemId(),
                    quiz.getHeritageName(),
                    quiz.getImageUrl(),
                    quiz.getDescription(),
                    quiz.getObjectImageUrl(),
                    quiz.getContent(),
                    choices,
                    quiz.getAnswer()
            );
        } catch (RuntimeException e) {
//            log.error("Error while fetching heritage mission for missionId: {}", missionId, e);
            throw new DatabaseOperationException("퀴즈 조회 중 오류가 발생했습니다: " + e.getMessage());
        }

    }
    @Transactional
    public HeritageMissionAnswerResponse submitHeritageMission(Long missionId, HeritageMissionAnswerRequest request) {

        if (request.getUserId() == null || request.getAnswers() == null) {
            throw new InvalidRequestException("유저 ID와 정답은 필수값입니다.");
        }

        HeritageProblem quiz = heritageMissionMapper.findByMissionId(missionId)
                .orElseThrow(() -> new MissionNotFoundException("해당 미션 ID의 퀴즈를 찾을 수 없습니다."));

        boolean isCorrect = quiz.getAnswer().equals(request.getAnswers());

        if (isCorrect) {
            // 카드 도감 등록
            int insertedRows = heritageMissionMapper.insertHeritageBook(request.getUserId(), quiz.getCardId());
            if (insertedRows == 0) {
                log.info("카드 도감 등록 여부: 이미 등록됨");
                heritageMissionMapper.insertLog(request.getUserId(), quiz.getCardId(), isCorrect);
                return new HeritageMissionAnswerResponse(true, quiz.getObjectImageUrl());
            }
            log.info("카드 도감 등록 여부: 성공");
        }


        // 꾸미백과 (풀이 기록) 저장
        Log myLog = heritageMissionMapper.findByLog(request.getUserId(), quiz.getCardId());
        log.error("FindByLog result: " + myLog);
        if (myLog == null) {
            heritageMissionMapper.insertLog(request.getUserId(), quiz.getCardId(), isCorrect);
        }

//        log.info("꾸미백과 등록 여부: 성공");
        return new HeritageMissionAnswerResponse(isCorrect, quiz.getObjectImageUrl());
    }

}