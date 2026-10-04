package com.dh.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponseDto {

    private Integer id;
    private String name;
    private String description;
    private String address;
    private int cost;
    private Character state;
    private CityDto city;
    private List<String> imageUrls;
    private List<CategoryDto> categories;
    private List<FeatureDto> features;
}