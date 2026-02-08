package com.dh.demo.service.impl;

import com.dh.demo.dto.PaisDto;
import com.dh.demo.entity.Pais;
import com.dh.demo.repository.PaisRepository;
import com.dh.demo.service.ICountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaisService implements ICountryService {

    private final PaisRepository paisRepository;

    public PaisService(PaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }

    @Override
    public PaisDto save(PaisDto paisDto) {

        Pais pais = mapToEntity(paisDto);
        Pais saved = paisRepository.save(pais);

        return mapToDto(saved);
    }

    @Override
    public Optional<PaisDto> findById(Integer id) {

        return paisRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    public PaisDto update(Integer id, PaisDto paisDto) {

        Pais pais = paisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found"));

        pais.setPaiName(paisDto.getPaiName());
        pais.setPaiState(paisDto.getPaiState());

        Pais updated = paisRepository.save(pais);

        return mapToDto(updated);
    }

    @Override
    public void delete(Integer id) {

        if (!paisRepository.existsById(id)) {
            throw new RuntimeException("Country not found");
        }

        paisRepository.deleteById(id);
    }

    @Override
    public List<PaisDto> findAll() {

        return paisRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private PaisDto mapToDto(Pais pais) {

        PaisDto dto = new PaisDto();
        dto.setPaiSec(pais.getPaiSec());
        dto.setPaiName(pais.getPaiName());
        dto.setPaiState(pais.getPaiState());

        return dto;
    }

    private Pais mapToEntity(PaisDto dto) {

        Pais pais = new Pais();
        pais.setPaiSec(dto.getPaiSec());
        pais.setPaiName(dto.getPaiName());
        pais.setPaiState(dto.getPaiState());

        return pais;
    }
}