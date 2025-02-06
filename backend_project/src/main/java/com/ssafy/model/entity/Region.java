package com.ssafy.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {
    private Long regionId;
    private String regionName;
    private String boundary;
}
