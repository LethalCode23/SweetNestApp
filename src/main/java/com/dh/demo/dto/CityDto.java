package com.dh.demo.dto;

public class CityDto {

    private Integer citSec;
    private String citName;
    private Integer citDepSec;
    private Character citState;

    public CityDto(Integer citSec, String citName, Integer citDepSec, Character citState) {
        this.citSec = citSec;
        this.citName = citName;
        this.citDepSec = citDepSec;
        this.citState = citState;
    }


    public Integer getCitSec() {
        return citSec;
    }

    public void setCitSec(Integer citSec) {
        this.citSec = citSec;
    }

    public String getCitName() {
        return citName;
    }

    public void setCitName(String citName) {
        this.citName = citName;
    }

    public Integer getCitDepSec() {
        return citDepSec;
    }

    public void setCitDepSec(Integer citDepSec) {
        this.citDepSec = citDepSec;
    }

    public Character getCitState() {
        return citState;
    }

    public void setCitState(Character citState) {
        this.citState = citState;
    }

}