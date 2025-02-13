package com.ssafy.model.service.fcm;

import com.ssafy.controller.fcm.FcmDto;
import com.ssafy.model.mapper.dao.FcmDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmServiceImpl implements FcmService {

    private final FcmDao fcmDao;

    @Override
    @Transactional
    public int addToken(Long userId, String fcmToken) {
        if (userId == null || fcmToken == null) {
            throw new IllegalArgumentException("사용자 ID와 토큰은 필수값입니다.");
        }

        try {
            log.info("FCM 토큰 등록/업데이트 시도: userId={}", userId);
            FcmDto fcmDto = new FcmDto(userId, fcmToken);
            int result = fcmDao.upsert(fcmDto);

            if (result > 0) {
                log.info("FCM 토큰 처리 성공: userId={}", userId);
            } else {
                log.warn("FCM 토큰 처리 실패: userId={}", userId);
            }

            return result;
        } catch (Exception e) {
            log.error("FCM 토큰 처리 중 오류 발생: userId={}", userId, e);
            throw new RuntimeException("FCM 토큰 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional
    public int removeToken(Long userId, String fcmToken) {
        if (userId == null || fcmToken == null) {
            throw new IllegalArgumentException("사용자 ID와 토큰은 필수값입니다.");
        }

        try {
            FcmDto fcm = new FcmDto(userId, fcmToken);
            return fcmDao.delete(fcm);
        } catch (Exception e) {
            log.error("FCM 토큰 삭제 중 오류 발생: userId={}", userId, e);
            throw new RuntimeException("FCM 토큰 삭제 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<String> getTokensByUserId(Long userId) {
        return fcmDao.selectTokensByUserId(userId);
    }

    @Override
    public List<String> getAllTokens() {
        return fcmDao.selectAllTokens();
    }

    @Override
    public List<Long> getAllUserIds() {
        return fcmDao.selectAllUserIds();
    }

    @Override
    public List<Long> getStudentIdsByRoomId(Long roomId) {
        return fcmDao.selectStudentIdsByRoomId(roomId);
    }

    @Override
    public List<String> getTokensByRoomId(Long roomId) {
        return fcmDao.selectTokensByRoomId(roomId);
    }
}
//@Service
//@RequiredArgsConstructor
//public class FcmServiceImpl implements FcmService {
//
//    private final FcmDao fcmDao;
//    @Override
//    public int addToken(Long userId, String fcmToken) {
//        FcmDto fcm = new FcmDto(userId, fcmToken);
//        try {
//            return fcmDao.upsert(fcm);
//        } catch (Exception e) {
//            throw new RuntimeException("FCM 토큰 추가 중 오류 발생", e);
//        }
//    }
//
//    @Override
//    public int removeToken(Long userId, String fcmToken) {
//        FcmDto fcm = new FcmDto(userId, fcmToken);
//        return fcmDao.delete(fcm);
//    }
//
//    @Override
//    public List<String> getTokensByUserId(Long userId) {
//        return fcmDao.selectTokensByUserId(userId);
//    }
//
//    @Override
//    public List<String> getAllTokens() {
//        return fcmDao.selectAllTokens();
//    }
//
//    @Override
//    public List<Long> getAllUserIds() {
//        return fcmDao.selectAllUserIds();
//    }
//
//    @Override
//    public List<Long> getStudentIdsByRoomId(Long roomId) {
//        return fcmDao.selectStudentIdsByRoomId(roomId);
//    }
//
//    @Override
//    public List<String> getTokensByRoomId(Long roomId) {
//
//        return fcmDao.selectTokensByRoomId(roomId);
//    }
//}