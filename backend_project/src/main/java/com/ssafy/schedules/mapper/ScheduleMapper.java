package com.ssafy.schedules.mapper;

import com.ssafy.schedules.model.Schedule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    List<Schedule> findByRoomId(Long roomId);
    Schedule findByRoomIdAndScheduleId(Long roomId, Long scheduleId);
    void insert(Schedule schedule);
    void update(Schedule schedule);
    void delete(Long scheduleId);
}
