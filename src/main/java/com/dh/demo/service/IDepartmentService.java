package com.dh.demo.service;

import com.dh.demo.dto.DepartmentDto;
import java.util.List;
import java.util.Optional;

public interface IDepartmentService {

    DepartmentDto save(DepartmentDto departmentDto);

    Optional<DepartmentDto> findById(Integer id);

    DepartmentDto update(Integer id, DepartmentDto departmentDto);

    void delete(Integer id);

    List<DepartmentDto> findAll();
}