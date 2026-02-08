package com.dh.demo.service.impl;

import com.dh.demo.dto.CountryResponseDto;
import com.dh.demo.dto.DepartmentDto;
import com.dh.demo.entity.Department;
import com.dh.demo.entity.Pais;
import com.dh.demo.repository.DepartmentRepository;
import com.dh.demo.repository.PaisRepository;
import com.dh.demo.service.IDepartmentService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentService implements IDepartmentService {

    private final DepartmentRepository departmentRepository;
    private final PaisRepository paisRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
                             PaisRepository paisRepository) {
        this.departmentRepository = departmentRepository;
        this.paisRepository = paisRepository;
    }

    @Override
    public DepartmentDto save(DepartmentDto departmentDto) {

        Pais pais = paisRepository.findById(departmentDto.getCountryResponseDto().getPaiSec())
                .orElseThrow(() -> new RuntimeException("Country not found"));

        Department department = new Department();
        department.setDepName(departmentDto.getDepName());
        department.setDepState(departmentDto.getDepState());
        department.setPais(pais);

        Department saved = departmentRepository.save(department);

        return mapToDto(saved);
    }

    @Override
    public Optional<DepartmentDto> findById(Integer id) {

        return departmentRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    public DepartmentDto update(Integer id, DepartmentDto departmentDto) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Pais pais = paisRepository.findById(departmentDto.getCountryResponseDto().getPaiSec())
                .orElseThrow(() -> new RuntimeException("Country not found"));

        department.setDepName(departmentDto.getDepName());
        department.setDepState(departmentDto.getDepState());
        department.setPais(pais);

        Department updated = departmentRepository.save(department);

        return mapToDto(updated);
    }

    @Override
    public void delete(Integer id) {

        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found");
        }

        departmentRepository.deleteById(id);
    }

    @Override
    public List<DepartmentDto> findAll() {

        return departmentRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private DepartmentDto mapToDto(Department department) {

        DepartmentDto dto = new DepartmentDto();

        dto.setDepSec(department.getDepSec());
        dto.setDepName(department.getDepName());
        dto.setDepState(department.getDepState());
        dto.setCountryResponseDto(new CountryResponseDto(department.getPais().getPaiSec(), department.getPais().getPaiName()));

        return dto;
    }
}