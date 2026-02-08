package com.dh.demo.service;

import com.dh.demo.dto.CityDto;
import java.util.List;
import java.util.Optional;

public interface ICityService {

    CityDto save(CityDto cityDto);

    CityDto update(Integer id, CityDto cityDto);

    Optional<CityDto> findById(Integer id);

    List<CityDto> findAll();

    void delete(Integer id);
}