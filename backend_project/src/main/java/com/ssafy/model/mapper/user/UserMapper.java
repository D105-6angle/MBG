package com.ssafy.model.mapper.user;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    String getTeacherNameById(Long teacherId);

}
