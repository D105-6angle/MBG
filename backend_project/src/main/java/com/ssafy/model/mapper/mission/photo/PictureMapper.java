package com.ssafy.model.mapper.mission.photo;


import com.ssafy.model.entity.Picture;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PictureMapper {
    int insertPicture(Picture picture);
}