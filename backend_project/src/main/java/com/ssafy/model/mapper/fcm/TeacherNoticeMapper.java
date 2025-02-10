package com.ssafy.model.mapper.fcm;

import com.ssafy.controller.TeacherNotice.TeacherNoticeDto;
import com.ssafy.model.entity.TeacherNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherNoticeMapper {
    List<TeacherNoticeDto> getNoticesByRoomId(Long roomId);
    int insertNotice(TeacherNotice notice);
    TeacherNotice selectNotice(@Param("noticeId") Long noticeId, @Param("roomId") Long roomId);
    List<TeacherNotice> selectNoticesByRoomId(@Param("roomId") Long roomId);
    int updateNoticeStatus(@Param("noticeId") Long noticeId, @Param("status") boolean status);
}
