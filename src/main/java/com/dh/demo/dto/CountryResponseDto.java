package com.dh.demo.dto;
public class CountryResponseDto {

    private final Integer paiSec;
    private final String paiName;

    public CountryResponseDto(Integer paiSec, String paiName) {
        this.paiSec = paiSec;
        this.paiName = paiName;
    }

    public Integer getPaiSec() {
        return paiSec;
    }

    public String getPaiName() {
        return paiName;
    }
}