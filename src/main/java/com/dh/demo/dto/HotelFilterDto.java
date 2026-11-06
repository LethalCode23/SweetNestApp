package com.dh.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class HotelFilterDto {
    private String citName;
    private Integer minCost;
    private Integer maxCost;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private List<Integer> categoryIds;
}