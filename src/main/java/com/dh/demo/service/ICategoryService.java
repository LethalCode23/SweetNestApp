package com.dh.demo.service;

import com.dh.demo.dto.CategoryDto;
import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    CategoryDto save(CategoryDto categoryDto);

    Optional<CategoryDto> findById(Integer id);

    CategoryDto update(Integer id, CategoryDto categoryDto);

    void delete(Integer id);

    List<CategoryDto> findAll();
}