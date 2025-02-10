package com.ssafy.model.mapper.schedule;

import com.ssafy.model.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    List<Schedule> findByRoomId(Long roomId);
    Schedule findByRoomIdAndScheduleId(Long roomId, Long scheduleId);
    void insert(Schedule schedule);
    void update(Schedule schedule);
    void delete(Long scheduleId);

    List<Schedule> findUpcomingSchedules(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
