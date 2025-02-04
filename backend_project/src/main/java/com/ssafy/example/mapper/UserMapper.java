package com.ssafy.example.mapper;

import com.ssafy.example.model.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    List<User> getAllUsers();
}
