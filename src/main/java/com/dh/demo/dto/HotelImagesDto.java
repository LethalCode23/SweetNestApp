package com.dh.demo.dto;

public class HotelImagesDto {

    private Integer hotImgSec;
    private Integer hotSec;
    private int hotImgPri;

    public HotelImagesDto(Integer hotImgSec, Integer hotSec, int hotImgPri) {
        this.hotImgSec = hotImgSec;
        this.hotSec = hotSec;
        this.hotImgPri = hotImgPri;
    }

    public Integer getHotImgSec() {
        return hotImgSec;
    }

    public void setHotImgSec(Integer hotImgSec) {
        this.hotImgSec = hotImgSec;
    }

    public Integer getHotSec() {
        return hotSec;
    }

    public void setHotSec(Integer hotSec) {
        this.hotSec = hotSec;
    }

    public int getHotImgPri() {
        return hotImgPri;
    }

    public void setHotImgPri(int hotImgPri) {
        this.hotImgPri = hotImgPri;
    }
}