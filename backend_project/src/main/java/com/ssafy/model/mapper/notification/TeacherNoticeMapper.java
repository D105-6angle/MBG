package com.ssafy.model.mapper.notification;

import com.ssafy.controller.room.RoomResponse;
import com.ssafy.model.entity.TeacherNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherNoticeMapper {
    void insertNotice(TeacherNotice notice);
    void updateNoticeStatus(@Param("noticeId") Long noticeId, @Param("status") boolean status);
}
