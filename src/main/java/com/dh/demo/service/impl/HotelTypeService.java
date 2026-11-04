package com.dh.demo.service.impl;

import com.dh.demo.dto.HotelTypeDto;
import com.dh.demo.entity.HotelType;
import com.dh.demo.exception.DependentRecordsExistException;
import com.dh.demo.exception.HotelTypeNotFoundException;
import com.dh.demo.mapper.HotelTypeMapper;
import com.dh.demo.repository.HotelTypeRepository;
import com.dh.demo.service.IHotelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelTypeService implements IHotelTypeService {

    private final HotelTypeRepository hotelTypeRepository;
    private final HotelTypeMapper mapper;

    @Override
    @Transactional
    public HotelTypeDto create(HotelTypeDto dto) {
        return mapper.toDto(hotelTypeRepository.save(mapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public HotelTypeDto update(Integer id, HotelTypeDto dto) {

        HotelType entity = getEntity(id);
        mapper.updateEntity(dto, entity);

        return mapper.toDto(hotelTypeRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public HotelTypeDto findById(Integer id) {
        return mapper.toDto(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelTypeDto> findAll() {
        return hotelTypeRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelTypeDto> findActives() {
        return hotelTypeRepository.findByTypStateOrderByTypNameAsc('A')
                .stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public void delete(Integer id) {

        HotelType entity = getEntity(id);

        if (entity.getHotels() != null && !entity.getHotels().isEmpty()) {
            throw new DependentRecordsExistException(
                    "No se puede eliminar el tipo: tiene hoteles asociados");
        }

        hotelTypeRepository.delete(entity);
    }

    private HotelType getEntity(Integer id) {
        return hotelTypeRepository.findById(id)
                .orElseThrow(() -> new HotelTypeNotFoundException("Tipo de alojamiento no encontrado con id: " + id));
    }
}
