package com.ssafy.model.mapper.room;

import com.ssafy.model.entity.Room;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoomMapper {
    void insertRoom(Room room);
    Room selectRoomById(long roomId);
}