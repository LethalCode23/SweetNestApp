package com.dh.demo.mapper;

import com.dh.demo.dto.CityDto;
import com.dh.demo.entity.City;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    public CityDto toDto(City city) {

        if (city == null) {
            return null;
        }

        return CityDto.builder()
                .citSec(city.getCitSec())
                .citName(city.getCitName())
                .citState(city.getCitState())
                .citDepSec(city.getDepartment().getDepSec())
                .build();
    }
}