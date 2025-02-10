package com.ssafy.controller.room.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 조(Group) 생성/수정 시 사용하는 Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest {
    private int groupNo;       // 조 번호
    private String groupName;  // 조 이름
    // 추가로 필요한 필드가 있으면 확장
}