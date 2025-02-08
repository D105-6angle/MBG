package com.ssafy.model.service.fcm;

import java.util.List;

import com.ssafy.model.entity.Alarm;
import com.ssafy.model.mapper.fcm.AlarmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmService {

    @Autowired
    private AlarmMapper alarmMapper;

    public int addAlarm(Alarm alarm) {
        return alarmMapper.insert(alarm);
    }

    public List<Alarm> getAlarmsByUserId(Long userId) {
        return alarmMapper.selectByUserId(userId);
    }
}