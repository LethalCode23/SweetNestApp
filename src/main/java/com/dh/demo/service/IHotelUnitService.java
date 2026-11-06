package com.dh.demo.service;

import com.dh.demo.dto.HotelUnitDto;
import java.util.List;

public interface IHotelUnitService {

    HotelUnitDto create(HotelUnitDto dto);

    HotelUnitDto update(Integer id, HotelUnitDto dto);

    HotelUnitDto findById(Integer id);

    /** Unidades activas de un hotel. */
    List<HotelUnitDto> findByHotel(Integer hotSec);

    void delete(Integer id);
}
