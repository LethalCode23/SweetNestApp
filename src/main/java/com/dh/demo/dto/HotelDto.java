package com.dh.demo.dto;

import java.util.List;

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
    private List<CategoryDto> categories;

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
                    Integer hotCitSec, String hotCitName, List<HotelImagesDto> hotelImagesUrl, List<Integer> categoryIds, List<CategoryDto> categories) {
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
    }

    public Integer getHotSec() {
        return hotSec;
    }

    public void setHotSec(Integer hotSec) {
        this.hotSec = hotSec;
    }

    public String getHotName() {
        return hotName;
    }

    public void setHotName(String hotName) {
        this.hotName = hotName;
    }

    public String getHotDescription() {
        return hotDescription;
    }

    public void setHotDescription(String hotDescription) {
        this.hotDescription = hotDescription;
    }

    public String getHotAddress() {
        return hotAddress;
    }

    public void setHotAddress(String hotAddress) {
        this.hotAddress = hotAddress;
    }

    public int getHotCost() {
        return hotCost;
    }

    public void setHotCost(int hotCost) {
        this.hotCost = hotCost;
    }

    public Character getHotState() {
        return hotState;
    }

    public void setHotState(Character hotState) {
        this.hotState = hotState;
    }

    public Integer getHotCitSec() {
        return hotCitSec;
    }

    public void setHotCitSec(Integer hotCitSec) {
        this.hotCitSec = hotCitSec;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<CategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }

    public String getHotCitName() {
        return hotCitName;
    }

    public void setHotCitName(String hotCitName) {
        this.hotCitName = hotCitName;
    }

    public List<HotelImagesDto> getHotelImagesUrl() {
        return hotelImagesUrl;
    }

    public void setHotelImagesUrl(List<HotelImagesDto> hotelImagesUrl) {
        this.hotelImagesUrl = hotelImagesUrl;
    }
}