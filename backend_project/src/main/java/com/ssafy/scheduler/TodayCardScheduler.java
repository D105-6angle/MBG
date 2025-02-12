package com.ssafy.scheduler;

import com.ssafy.model.entity.TodayCard;
import com.ssafy.model.mapper.todaycard.TodayCardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TodayCardScheduler {
    private final TodayCardMapper todayCardMapper;

    // 매일 지정된 시간에 오늘의 카드 업데이트
    // "초 분 시 일 월 요일"
    // 매일 00시 00분 00초에 오늘의 카드 업데이트
    @Scheduled(cron ="0 0 0 * * *")
    public void updateTodayCards() {
        // 1. 랜덤 조합 선택
        List<Map<String, Object>> combinations = todayCardMapper.selectRandomCombinations();

        // 2. TodayCard 객체로 변환
        List<TodayCard> todayCards = combinations.stream()
                .map(combo -> TodayCard.builder().cardId(((Number) combo.get("card_id")).longValue())
                        .missionId(((Number) combo.get("mission_id")).longValue())
                        .createdAt(LocalDate.now())
                        .build()).collect(Collectors.toList());

        // 3. 저장
        if (!todayCards.isEmpty()) {
            todayCardMapper.insertTodayCards(todayCards);
        }
    }
}
