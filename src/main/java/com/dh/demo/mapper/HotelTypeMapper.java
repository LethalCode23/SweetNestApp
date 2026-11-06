package com.dh.demo.mapper;

import com.dh.demo.dto.HotelTypeDto;
import com.dh.demo.entity.HotelType;
import org.springframework.stereotype.Component;

@Component
public class HotelTypeMapper {

    public HotelTypeDto toDto(HotelType entity) {

        HotelTypeDto dto = new HotelTypeDto();
        dto.setTypSec(entity.getTypSec());
        dto.setTypName(entity.getTypName());
        dto.setTypMultiUnit(entity.getTypMultiUnit() == null ? null : String.valueOf(entity.getTypMultiUnit()));
        dto.setTypState(entity.getTypState() == null ? null : String.valueOf(entity.getTypState()));

        return dto;
    }

    public HotelType toEntity(HotelTypeDto dto) {

        HotelType entity = new HotelType();
        entity.setTypName(dto.getTypName());
        entity.setTypMultiUnit(toChar(dto.getTypMultiUnit()));
        entity.setTypState(dto.getTypState() == null ? 'A' : toChar(dto.getTypState()));

        return entity;
    }

    public void updateEntity(HotelTypeDto dto, HotelType entity) {

        entity.setTypName(dto.getTypName());
        entity.setTypMultiUnit(toChar(dto.getTypMultiUnit()));

        if (dto.getTypState() != null) {
            entity.setTypState(toChar(dto.getTypState()));
        }
    }

    private Character toChar(String value) {
        return (value == null || value.isBlank()) ? null : value.charAt(0);
    }
}
