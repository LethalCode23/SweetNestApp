package com.dh.demo.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelImagesDto {

    private Integer hotImgSec;
    private Integer hotSec;
    private String hotImgUrl;
    private int hotImgPri;
}