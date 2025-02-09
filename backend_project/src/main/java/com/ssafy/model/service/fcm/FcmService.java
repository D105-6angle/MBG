package com.ssafy.model.service.fcm;

import java.util.List;

public interface FcmService {

    int addToken(Long userId, String fcmToken);
    int removeToken(Long userId, String fcmToken);
    List<String> getTokensByUserId(Long userId);
    List<String> getAllTokens();
    List<Long> getAllUserIds(); // 추가된 메서드
    List<Long> getStudentIdsByRoomId(Long roomId);
    List<String> getTokensByRoomId(Long roomId);
}
