package com.ssafy.group.model;

import com.ssafy.mypage.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership {
    private Long userId;
    private Long roomId;
    private String codeId;
    private Integer groupNo;

    // 연관
    private User user;
    private Room room;
}
