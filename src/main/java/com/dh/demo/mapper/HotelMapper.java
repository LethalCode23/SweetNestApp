package com.dh.demo.mapper;

import com.dh.demo.dto.CategoryDto;
import com.dh.demo.dto.FeatureDto;
import com.dh.demo.dto.HotelResponseDto;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelImages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HotelMapper {

    private final CityMapper cityMapper;
    private final CategoryMapper categoryMapper;
    private final FeatureMapper featureMapper;

    public HotelResponseDto toDetailDto(Hotel hotel) {

        return HotelResponseDto.builder()
                .id(hotel.getHotSec())
                .name(hotel.getHotName())
                .description(hotel.getHotDescription())
                .address(hotel.getHotAddress())
                .cost(hotel.getHotCost())
                .state(hotel.getHotState())
                .city(cityMapper.toDto(hotel.getCity()))
                .imageUrls(mapImageUrls(hotel))
                .categories(mapCategories(hotel))
                .features(mapFeatures(hotel))
                .build();
    }

    private List<String> mapImageUrls(Hotel hotel) {

        if (hotel.getHotelImages() == null) {
            return List.of();
        }

        return hotel.getHotelImages().stream()
                .map(HotelImages::getHotImgUrl)
                .collect(Collectors.toList());
    }

    private List<CategoryDto> mapCategories(Hotel hotel) {

        if (hotel.getCategories() == null) {
            return List.of();
        }

        return hotel.getCategories().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<FeatureDto> mapFeatures(Hotel hotel) {

        if (hotel.getFeatures() == null) {
            return List.of();
        }

        return hotel.getFeatures().stream()
                .map(featureMapper::toDto)
                .collect(Collectors.toList());
    }
}