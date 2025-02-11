package com.ssafy.model.mapper.todaycard;

import com.ssafy.model.entity.TodayCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface TodayCardMapper {

    // 뽑은 조합을 테이블에 저장
    void insertTodayCards(List<TodayCard> todayCards);

    // 각 유적 장소별로 일화 카드와 위치 조합을 뽑는 함수
    @Select("""
       WITH RandomCombinations AS (
            SELECT hp.place_id, mp.mission_id, c.card_id,
                ROW_NUMBER() OVER (PARTITION BY hp.place_id ORDER BY RAND()) as rn
            FROM heritage_places hp INNER JOIN mission_positions mp ON hp.place_id = mp.place_id
            CROSS JOIN cards c WHERE mp.code_id = 'M002' AND c.code_id = 'M002')
        SELECT place_id, mission_id, card_id
        FROM RandomCombinations
        WHERE rn = 1
    """)
    List<Map<String, Object>> selectRandomCombinations();
}
