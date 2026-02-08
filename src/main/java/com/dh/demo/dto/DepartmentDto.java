package com.dh.demo.dto;

import com.dh.demo.entity.Department;

public class DepartmentDto {

    private Integer depSec;
    private String depName;
    private CountryResponseDto countryResponseDto;
    private Character depState;

    public DepartmentDto() {
    }

    public DepartmentDto(Integer depSec, String depName, CountryResponseDto countryResponseDto, Character depState) {
        this.depSec = depSec;
        this.depName = depName;
        this.countryResponseDto = countryResponseDto;
        this.depState = depState;
    }

    public Integer getDepSec() {
        return depSec;
    }

    public void setDepSec(Integer depSec) {
        this.depSec = depSec;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public CountryResponseDto getCountryResponseDto() {
        return countryResponseDto;
    }

    public void setCountryResponseDto(CountryResponseDto countryResponseDto) {
        this.countryResponseDto = countryResponseDto;
    }

    public Character getDepState() {
        return depState;
    }

    public void setDepState(Character depState) {
        this.depState = depState;
    }
}