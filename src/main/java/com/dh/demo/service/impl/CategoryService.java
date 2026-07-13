package com.dh.demo.service.impl;

import com.dh.demo.dto.CategoryDto;
import com.dh.demo.entity.Category;
import com.dh.demo.repository.CategoryRepository;
import com.dh.demo.service.ICategoryService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = new Category();
        category.setCatName(categoryDto.getCatName());

        if (categoryDto.getCatEst() == null) {
            category.setCatEst('A');
        } else {
            category.setCatEst(categoryDto.getCatEst());
        }

        categoryRepository.save(category);

        return mapToDto(category);
    }

    @Override
    public CategoryDto update(Integer id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setCatName(categoryDto.getCatName());
        category.setCatEst(categoryDto.getCatEst());

        categoryRepository.save(category);

        return mapToDto(category);
    }

    @Override
    public Optional<CategoryDto> findById(Integer id) {
        return categoryRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }

    private CategoryDto mapToDto(Category category) {

        CategoryDto dto = new CategoryDto();
        dto.setCatSec(category.getCatSec());
        dto.setCatName(category.getCatName());
        dto.setCatEst(category.getCatEst());

        return dto;
    }
}