package com.ssafy.model.mapper.problem;

import com.ssafy.model.entity.HeritageProblem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProblemMapper {
    int insertHeritageProblem(HeritageProblem dto);
}
