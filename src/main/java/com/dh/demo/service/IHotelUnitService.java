package com.dh.demo.service;

import com.dh.demo.dto.HotelUnitDto;
import java.time.LocalDate;
import java.util.List;

public interface IHotelUnitService {

    HotelUnitDto create(HotelUnitDto dto);

    HotelUnitDto update(Integer id, HotelUnitDto dto);

    HotelUnitDto findById(Integer id);

    /** Unidades activas de un hotel. */
    List<HotelUnitDto> findByHotel(Integer hotSec);

    /** Unidades activas del hotel sin reservas activas que se crucen con [checkIn, checkOut). */
    List<HotelUnitDto> findAvailable(Integer hotSec, LocalDate checkIn, LocalDate checkOut);

    void delete(Integer id);
}
