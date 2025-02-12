package com.ssafy.controller.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRoomResponse {

    @Schema(example = "9")
    private Long roomId;

    @Schema(example = "경복궁")
    private String location;

    @Schema(example = "6")
    private int numOfGroups;

}