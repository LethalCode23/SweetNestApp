package com.dh.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class HotelFilterDto {
    private String citName;
    private Integer minCost;
    private Integer maxCost;
    private List<Integer> categoryIds;
}