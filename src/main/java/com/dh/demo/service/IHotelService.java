package com.dh.demo.service;

import com.dh.demo.dto.HotelDto;
import com.dh.demo.entity.Hotel;
import java.util.List;
import java.util.Optional;

public interface IHotelService {

    HotelDto save(HotelDto hotelDto);

    HotelDto update(Integer id, HotelDto hotelDto);

    Optional<HotelDto> findById(Integer id);

    void delete(Integer id);

    List<HotelDto> findAll();
}