package com.dh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CityDto {

    private Integer citSec;
    private String citName;
    private Integer citDepSec;
    private Character citState;
}