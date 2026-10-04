package com.dh.demo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HotelDto {

    private Integer hotSec;
    private String hotName;
    private String hotDescription;
    private String hotAddress;
    private int hotCost;
    private Character hotState;
    private Integer hotCitSec;
    private String hotCitName;
    private List<HotelImagesDto> hotelImagesUrl;
    private List<Integer> categoryIds;
    private List<Integer> featureIds;
    private List<CategoryDto> categories;
    private List<FeatureDto> features;

    public HotelDto() {

    }

    public HotelDto(Integer hotSec, String hotName, String hotDescription, String hotAddress, int hotCost, Character hotState,
                    Integer hotCitSec, String hotCitName) {

        this.hotSec = hotSec;
        this.hotName = hotName;
        this.hotDescription = hotDescription;
        this.hotAddress = hotAddress;
        this.hotCost = hotCost;
        this.hotState = hotState;
        this.hotCitSec = hotCitSec;
        this.hotCitName = hotCitName;
    }

    public HotelDto(Integer hotSec, String hotName, String hotDescription, String hotAddress, int hotCost, Character hotState,
                    Integer hotCitSec, String hotCitName, List<HotelImagesDto> hotelImagesUrl, List<Integer> categoryIds,
                    List<CategoryDto> categories, List<FeatureDto> features) {

        this.hotSec = hotSec;
        this.hotName = hotName;
        this.hotDescription = hotDescription;
        this.hotAddress = hotAddress;
        this.hotCost = hotCost;
        this.hotState = hotState;
        this.hotCitSec = hotCitSec;
        this.hotelImagesUrl = hotelImagesUrl;
        this.categoryIds = categoryIds;
        this.categories = categories;
        this.hotCitName = hotCitName;
        this.features = features;
    }
}