package com.dh.demo.service;

import com.dh.demo.dto.HotelTypeDto;
import java.util.List;

public interface IHotelTypeService {

    HotelTypeDto create(HotelTypeDto dto);

    HotelTypeDto update(Integer id, HotelTypeDto dto);

    HotelTypeDto findById(Integer id);

    List<HotelTypeDto> findAll();

    /** Solo tipos activos, para el formulario y los filtros. */
    List<HotelTypeDto> findActives();

    void delete(Integer id);
}
