package com.ssafy.controller.room.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//조(Group) 생성
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest {
    private int groupNo;       // 조 번호
    private String groupName;  // 조 이름

}