package com.ssafy.controller.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomRequest {

    @NotBlank(message = "방 이름은 필수입니다.")
    private String roomName;

    @NotBlank(message = "위치는 필수입니다.")
    private String location;

    @Min(value = 1, message = "최소 한 개 이상의 그룹이 있어야 합니다.")
    private int numOfGroups;

}
