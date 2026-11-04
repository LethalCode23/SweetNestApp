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
    private Integer typeId;
    private String typeName;
    /** 'S' la reserva requiere confirmacion, 'N' reserva inmediata. */
    private Character confirmReq;
    /** Anfitrion del hotel (users.userSec). Null = sin asignar. */
    private Long hostId;
    private List<String> imageUrls;
    private List<CategoryDto> categories;
    private List<FeatureDto> features;
}