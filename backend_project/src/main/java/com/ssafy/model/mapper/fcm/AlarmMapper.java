package com.ssafy.model.mapper.fcm;

import com.ssafy.model.entity.Alarm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlarmMapper {
    int insert(Alarm alarm);
    List<Alarm> selectByUserId(Long userId);
}
