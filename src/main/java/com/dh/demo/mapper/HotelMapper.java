package com.dh.demo.mapper;

import com.dh.demo.dto.CategoryDto;
import com.dh.demo.dto.FeatureDto;
import com.dh.demo.dto.HotelDto;
import com.dh.demo.dto.HotelImagesDto;
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
                .typeId(hotel.getType() != null ? hotel.getType().getTypSec() : null)
                .typeName(hotel.getType() != null ? hotel.getType().getTypName() : null)
                .confirmReq(hotel.getHotConfirmReq())
                .hostId(hotel.getHost() != null ? hotel.getHost().getUserSec() : null)
                .imageUrls(mapImageUrls(hotel))
                .categories(mapCategories(hotel))
                .features(mapFeatures(hotel))
                .build();
    }

    /** Formato de lista: cards de busqueda, home y favoritos. */
    public HotelDto toListDto(Hotel hotel) {

        HotelDto dto = new HotelDto();

        dto.setHotSec(hotel.getHotSec());
        dto.setHotName(hotel.getHotName());
        dto.setHotDescription(hotel.getHotDescription());
        dto.setHotAddress(hotel.getHotAddress());
        dto.setHotCost(hotel.getHotCost());
        dto.setHotState(hotel.getHotState());
        dto.setHotCitSec(hotel.getCity() != null ? hotel.getCity().getCitSec() : null);
        dto.setHotCitName(hotel.getCity() != null ? hotel.getCity().getCitName() : null);
        dto.setTypeId(hotel.getType() != null ? hotel.getType().getTypSec() : null);
        dto.setTypeName(hotel.getType() != null ? hotel.getType().getTypName() : null);
        dto.setHotConfirmReq(hotel.getHotConfirmReq());
        dto.setHostId(hotel.getHost() != null ? hotel.getHost().getUserSec() : null);

        if (hotel.getHotelImages() != null && !hotel.getHotelImages().isEmpty()) {
            dto.setHotelImagesUrl(hotel.getHotelImages().stream()
                    .map(img -> new HotelImagesDto(
                            img.getHotImgSec(),
                            hotel.getHotSec(),
                            img.getHotImgUrl(),
                            img.getHotImgPri()))
                    .collect(Collectors.toList()));
        }

        if (hotel.getCategories() != null) {
            dto.setCategories(hotel.getCategories().stream()
                    .map(category -> {
                        CategoryDto cDto = new CategoryDto();
                        cDto.setCatSec(category.getCatSec());
                        cDto.setCatName(category.getCatName());
                        cDto.setCatEst(category.getCatEst());
                        return cDto;
                    })
                    .collect(Collectors.toList()));
        }

        if (hotel.getFeatures() != null) {
            dto.setFeatures(hotel.getFeatures().stream()
                    .map(feature -> {
                        FeatureDto iDto = new FeatureDto();
                        iDto.setFeaSec(feature.getFeaSec());
                        iDto.setFeaName(feature.getFeaName());
                        iDto.setFeaEst(feature.getFeaEst());
                        iDto.setFeaIconUrl(feature.getFeaIconUrl());
                        return iDto;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
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