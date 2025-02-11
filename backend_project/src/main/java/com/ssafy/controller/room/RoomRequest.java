package com.ssafy.controller.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomRequest {

    private Long teacherId;

    @NotBlank(message = "방 이름은 필수입니다.")
    private String roomName;

    @NotBlank(message = "위치는 필수입니다.")
    private String location;

    @Min(value = 1, message = "조의 수는 1 이상이어야 합니다.")
    private int numOfGroups;
}
