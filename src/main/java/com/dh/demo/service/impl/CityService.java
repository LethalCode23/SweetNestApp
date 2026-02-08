package com.dh.demo.service.impl;

import com.dh.demo.dto.CityDto;
import com.dh.demo.entity.City;
import com.dh.demo.entity.Department;
import com.dh.demo.repository.CityRepository;
import com.dh.demo.repository.DepartmentRepository;
import com.dh.demo.service.ICityService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CityService implements ICityService {

    private final CityRepository cityRepository;
    private final DepartmentRepository departmentRepository;

    public CityService(CityRepository cityRepository,
                       DepartmentRepository departmentRepository) {
        this.cityRepository = cityRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public CityDto save(CityDto cityDto) {

        Department department = departmentRepository.findById(cityDto.getCitDepSec())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        City city = new City();
        city.setCitName(cityDto.getCitName());
        city.setCitState(cityDto.getCitState());
        city.setDepartment(department);

        cityRepository.save(city);

        return mapToDto(city);
    }

    @Override
    public CityDto update(Integer id, CityDto cityDto) {

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));

        Department department = departmentRepository.findById(cityDto.getCitDepSec())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        city.setCitName(cityDto.getCitName());
        city.setCitState(cityDto.getCitState());
        city.setDepartment(department);

        cityRepository.save(city);

        return mapToDto(city);
    }

    @Override
    public Optional<CityDto> findById(Integer id) {
        return cityRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    public List<CityDto> findAll() {
        return cityRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void delete(Integer id) {
        cityRepository.deleteById(id);
    }

    private CityDto mapToDto(City city) {
        return new CityDto(
                city.getCitSec(),
                city.getCitName(),
                city.getDepartment().getDepSec(),
                city.getCitState()
        );
    }
}