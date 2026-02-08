package com.dh.demo.dto;

public class HotelDto {

    private Integer hotSec;
    private String hotName;
    private String hotDescription;
    private String hotAddress;
    private int hotCost;
    private Character hotState;
    private Integer hotCitSec;
    private String hotImgUrl;

    public HotelDto() {

    }

    public HotelDto(Integer hotSec, String hotName, String hotDescription, String hotAddress, int hotCost, Character hotState,
                    Integer hotCitSec, String hotImgUrl) {
        this.hotSec = hotSec;
        this.hotName = hotName;
        this.hotDescription = hotDescription;
        this.hotAddress = hotAddress;
        this.hotCost = hotCost;
        this.hotState = hotState;
        this.hotCitSec = hotCitSec;
        this.hotImgUrl = hotImgUrl;
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

    public String getHotImgUrl() {
        return hotImgUrl;
    }

    public void setHotImgUrl(String hotImgUrl) {
        this.hotImgUrl = hotImgUrl;
    }
}