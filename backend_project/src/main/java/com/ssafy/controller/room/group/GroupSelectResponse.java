package com.ssafy.controller.room.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSelectResponse {
    private long userId;
    private long roomId;
    private int groupNo;
    private String codeId;
}