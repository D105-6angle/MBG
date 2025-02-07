package com.ssafy.model.service.fcm;

import com.ssafy.controller.fcm.FcmDto;
import com.ssafy.model.dao.FcmDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FcmServiceImpl implements FcmService {

    @Autowired
    private FcmDao fcmDao;

    @Override
    public int addToken(Long userId, String fcmToken) {
        FcmDto fcm = new FcmDto(userId, fcmToken);
        try {
            return fcmDao.insert(fcm);
        } catch (Exception e) {
            throw new RuntimeException("FCM 토큰 추가 중 오류 발생", e);
        }
    }

    @Override
    public int removeToken(Long userId, String fcmToken) {
        FcmDto fcm = new FcmDto(userId, fcmToken);
        return fcmDao.delete(fcm);
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
    public List<String> getAllUserIds() {
        return fcmDao.selectAllUserIds();
    }
}