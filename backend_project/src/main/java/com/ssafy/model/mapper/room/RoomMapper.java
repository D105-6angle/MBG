package com.ssafy.model.mapper.room;

import com.ssafy.model.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoomMapper {
    int insertRoom(Room room);
    Room selectRoomById(@Param("roomId") Long roomId);

    Room selectRoomByInviteCode(@Param("inviteCode") String inviteCode);

    int updateRoomGroupCount(Room room);

}