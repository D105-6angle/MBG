package com.ssafy.model.mapper.dao;

import com.ssafy.controller.fcm.FcmDto;
import com.ssafy.model.entity.FCM;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FcmDao {
    int insert(FcmDto fcm);
    int delete(FcmDto fcm);
    List<String> selectTokensByUserId(Long userId);
    List<String> selectAllTokens(); // 추가된 메서드
    List<Long> selectAllUserIds(); // 추가된 메서드
    // 특정 방의 학생 토큰 조회
    List<String> selectTokensByRoomId(@Param("roomId") Long roomId);
    int upsert(FcmDto fcmDto);
    // 특정 방의 학생 ID 조회
    List<Long> selectStudentIdsByRoomId(@Param("roomId") Long roomId);
}
