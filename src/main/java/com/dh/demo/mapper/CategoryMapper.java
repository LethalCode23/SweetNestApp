package com.dh.demo.mapper;

import com.dh.demo.dto.CategoryDto;
import com.dh.demo.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {

        if (category == null) {
            return null;
        }

        return CategoryDto.builder()
                .catSec(category.getCatSec())
                .catName(category.getCatName())
                .catEst(category.getCatEst())
                .build();
    }
}