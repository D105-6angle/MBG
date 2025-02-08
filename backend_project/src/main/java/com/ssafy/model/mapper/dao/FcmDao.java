package com.ssafy.model.mapper.dao;

import com.ssafy.controller.fcm.FcmDto;
import com.ssafy.model.entity.FCM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FcmDao {
    int insert(FcmDto fcm);
    int delete(FcmDto fcm);
    List<String> selectTokensByUserId(Long userId);
    List<String> selectAllTokens(); // 추가된 메서드
    List<String> selectAllUserIds(); // 추가된 메서드
    List<FCM> selectTokensByRoomId(Long roomId);
}
