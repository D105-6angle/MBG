package com.ssafy.model.service.notification;

import com.ssafy.controller.notification.CreateNoticeRequest;
import com.ssafy.controller.notification.NoticeDto;
import com.ssafy.model.entity.FCM;
import com.ssafy.model.entity.TeacherNotice;
import com.ssafy.model.mapper.dao.FcmDao;
import com.ssafy.model.mapper.notification.TeacherNoticeMapper;
import com.ssafy.model.service.FirebaseCloudMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class NoticeService{

    @Autowired
    private TeacherNoticeMapper teacherNoticeMapper;

    @Autowired
    private FcmDao fcmMapper;


    @Autowired
    private FirebaseCloudMessageService firebaseCloudMessageService;

    @Transactional
    public NoticeDto createNoticeAndSendFCM(CreateNoticeRequest request) {
        TeacherNotice notice = TeacherNotice.builder()
                .roomId(request.getRoomId())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        teacherNoticeMapper.insertNotice(notice);

        try {
            List<FCM> fcmTokens = fcmMapper.selectTokensByRoomId(notice.getRoomId());

            if (fcmTokens.isEmpty()) {
                log.warn("방 {}에 FCM 토큰이 없습니다.", notice.getRoomId());
                return NoticeDto.from(notice);
            }

            int successCount = 0;
            for (FCM fcm : fcmTokens) {
                try {
                    firebaseCloudMessageService.sendMessageTo(
                            fcm.getToken(),
                            notice.getTitle(),
                            notice.getContent()
                    );
                    successCount++;

                } catch (IOException e) {
                    log.error("FCM 발송 실패: token={}, error={}", fcm.getToken(), e.getMessage());
                }
            }

            if (successCount > 0) {
                teacherNoticeMapper.updateNoticeStatus(notice.getNoticeId(), true);
                notice.setStatus(true);
            }

            return NoticeDto.from(notice);

        } catch (Exception e) {
            log.error("알림 발송 중 오류 발생: {}", e.getMessage());
            return NoticeDto.from(notice);
        }
    }
}