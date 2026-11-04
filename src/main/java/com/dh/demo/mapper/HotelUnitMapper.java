package com.dh.demo.mapper;

import com.dh.demo.dto.HotelUnitDto;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelUnit;
import org.springframework.stereotype.Component;

@Component
public class HotelUnitMapper {
    public HotelUnitDto toDto(HotelUnit entity) {

        HotelUnitDto dto = new HotelUnitDto();
        dto.setUniSec(entity.getUniSec());
        dto.setUniName(entity.getUniName());
        dto.setUniCapacity(entity.getUniCapacity());
        dto.setUniPrice(entity.getUniPrice());
        dto.setUniState(entity.getUniState() == null ? null : String.valueOf(entity.getUniState()));
        dto.setHotSec(entity.getHotel() != null ? entity.getHotel().getHotSec() : null);

        return dto;
    }

    public HotelUnit toEntity(HotelUnitDto dto, Hotel hotel) {

        HotelUnit entity = new HotelUnit();
        entity.setHotel(hotel);
        entity.setUniName(dto.getUniName());
        entity.setUniCapacity(dto.getUniCapacity());
        entity.setUniPrice(dto.getUniPrice());
        entity.setUniState(dto.getUniState() == null ? 'A' : dto.getUniState().charAt(0));

        return entity;
    }

    public void updateEntity(HotelUnitDto dto, HotelUnit entity) {

        entity.setUniName(dto.getUniName());
        entity.setUniCapacity(dto.getUniCapacity());
        entity.setUniPrice(dto.getUniPrice());
        if (dto.getUniState() != null) {
            entity.setUniState(dto.getUniState().charAt(0));
        }
    }
}
