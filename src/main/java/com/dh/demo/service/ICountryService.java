package com.dh.demo.service;

import com.dh.demo.dto.PaisDto;
import java.util.List;
import java.util.Optional;

public interface ICountryService {

    PaisDto save(PaisDto paisDto);

    Optional<PaisDto> findById(Integer id);

    PaisDto update(Integer id, PaisDto paisDto);

    void delete(Integer id);

    List<PaisDto> findAll();
}