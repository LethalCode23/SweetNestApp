package com.dh.demo.dto;

public class PaisDto {

    private Integer paiSec;
    private String paiName;
    private Character paiState;

    public PaisDto() {
    }

    public PaisDto(Integer paiSec, String paiName, Character paiState) {
        this.paiSec = paiSec;
        this.paiName = paiName;
        this.paiState = paiState;
    }

    public Integer getPaiSec() {
        return paiSec;
    }

    public void setPaiSec(Integer paiSec) {
        this.paiSec = paiSec;
    }

    public String getPaiName() {
        return paiName;
    }

    public void setPaiName(String paiName) {
        this.paiName = paiName;
    }

    public Character getPaiState() {
        return paiState;
    }

    public void setPaiState(Character paiState) {
        this.paiState = paiState;
    }
}