package com.dh.demo.service.impl;

import com.dh.demo.dto.*;
import com.dh.demo.entity.Category;
import com.dh.demo.entity.City;
import com.dh.demo.entity.Feature;
import com.dh.demo.entity.Hotel;
import com.dh.demo.exception.HotelNotFoundException;
import com.dh.demo.mapper.FeatureMapper;
import com.dh.demo.mapper.HotelMapper;
import com.dh.demo.repository.CategoryRepository;
import com.dh.demo.repository.CityRepository;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.service.IHotelService;
import com.dh.demo.specification.HotelSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HotelService implements IHotelService {

    private final HotelRepository repository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final FeatureService featureService;
    private final FeatureMapper featureMapper;
    private final HotelMapper hotelMapper;

    @Override
    public HotelDto save(HotelDto hotelDto) {

        Hotel hotel = new Hotel();

        hotel.setHotName(hotelDto.getHotName());
        hotel.setHotDescription(hotelDto.getHotDescription());
        hotel.setHotAddress(hotelDto.getHotAddress());
        hotel.setHotCost(hotelDto.getHotCost());
        hotel.setHotState(hotelDto.getHotState());

        City city = cityRepository.findById(hotelDto.getHotCitSec())
                .orElseThrow(() -> new RuntimeException("City not found"));

        if (hotelDto.getCategoryIds() != null && !hotelDto.getCategoryIds().isEmpty()) {

            List<Category> categories = categoryRepository.findAllById(hotelDto.getCategoryIds());
            hotel.setCategories(categories);
        }

        if (hotelDto.getFeatureIds() != null && !hotelDto.getFeatureIds().isEmpty()) {

            List<FeatureDto> featuresDto = featureService.findByAllId(hotelDto.getFeatureIds());

            List<Feature> features = featuresDto
                    .stream()
                    .map(featureMapper::toEntity)
                    .collect(Collectors.toList());

            hotel.setFeatures(features);
        }

        hotel.setCity(city);
        Hotel savedHotel = repository.save(hotel);

        return new HotelDto(
                savedHotel.getHotSec(),
                savedHotel.getHotName(),
                savedHotel.getHotDescription(),
                savedHotel.getHotAddress(),
                savedHotel.getHotCost(),
                savedHotel.getHotState(),
                savedHotel.getCity().getCitSec(),
                savedHotel.getCity().getCitName()
        );
    }


    @Override
    public Optional<HotelDto> findById(Integer id) {

        return repository.findById(id)
                .map(hotel -> new HotelDto(
                        hotel.getHotSec(),
                        hotel.getHotName(),
                        hotel.getHotDescription(),
                        hotel.getHotAddress(),
                        hotel.getHotCost(),
                        hotel.getHotState(),
                        hotel.getCity() != null ? hotel.getCity().getCitSec() : null,
                        hotel.getCity() != null ? hotel.getCity().getCitName() : null
                ));
    }

    @Override
    public HotelDto update(Integer id, HotelDto hotelDto) {

        Hotel hotel = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel no found"));

        City city = cityRepository.findById(hotelDto.getHotCitSec())
                .orElseThrow(() -> new RuntimeException("City no found"));

        hotel.setHotName(hotelDto.getHotName());
        hotel.setHotDescription(hotelDto.getHotDescription());
        hotel.setHotAddress(hotelDto.getHotAddress());
        hotel.setHotCost(hotelDto.getHotCost());
        hotel.setCity(city);
        hotel.setHotState(hotelDto.getHotState());

        if (hotelDto.getCategoryIds() != null && !hotelDto.getCategoryIds().isEmpty()) {

            List<Category> categories = categoryRepository.findAllById(hotelDto.getCategoryIds());
            hotel.setCategories(categories);
        }

        if (hotelDto.getFeatureIds() != null && !hotelDto.getFeatureIds().isEmpty()) {

            List<FeatureDto> featuresDto = featureService.findByAllId(hotelDto.getFeatureIds());

            List<Feature> features = featuresDto
                    .stream()
                    .map(featureMapper::toEntity)
                    .collect(Collectors.toList());

            hotel.setFeatures(features);
        }

        Hotel updated = repository.save(hotel);

        return null;
    }

    @Override
    public void delete(Integer id) {
        this.repository.deleteById(id);
    }

    @Override
    public Page<HotelDto> findAll(Pageable pageable) {

        Page<Hotel> hotels = repository.findAll(pageable);
        return hotels.map(this::mapToDto);
    }

    @Override
    public Page<HotelDto> findByHotel(HotelFilterDto hotelFilterDto, Pageable pageable) {

        Specification<Hotel> specification = HotelSpecification.withFilters(hotelFilterDto);
        return repository.findAll(specification, pageable).map(this::mapToDto);
    }

    private HotelDto mapToDto(Hotel hotel) {

        HotelDto dto = new HotelDto();

        dto.setHotSec(hotel.getHotSec());
        dto.setHotName(hotel.getHotName());
        dto.setHotDescription(hotel.getHotDescription());
        dto.setHotAddress(hotel.getHotAddress());
        dto.setHotCost(hotel.getHotCost());
        dto.setHotState(hotel.getHotState());

        dto.setHotCitSec(
                hotel.getCity() != null
                        ? hotel.getCity().getCitSec()
                        : null
        );

        dto.setHotCitName(
                hotel.getCity() != null
                        ? hotel.getCity().getCitName()
                        : null
        );

        if (hotel.getHotelImages() != null && !hotel.getHotelImages().isEmpty()) {

            List<HotelImagesDto> imagesUrls = hotel.getHotelImages().stream()
                    .map(img -> new HotelImagesDto(
                            img.getHotImgSec(),
                            hotel.getHotSec(),
                            img.getHotImgUrl(),
                            img.getHotImgPri()
                    ))
                    .collect(Collectors.toList());

            dto.setHotelImagesUrl(imagesUrls);
        }

        if (hotel.getCategories() != null) {

            List<CategoryDto> categoryDto = hotel.getCategories().stream()
                    .map(category -> {
                        CategoryDto cDto = new CategoryDto();
                        cDto.setCatSec(category.getCatSec());
                        cDto.setCatName(category.getCatName());
                        cDto.setCatEst(category.getCatEst());
                        return cDto;
                    })
                    .collect(Collectors.toList());

            dto.setCategories(categoryDto);
        }

        if (hotel.getFeatures() != null) {

            List<FeatureDto> featureDto = hotel.getFeatures().stream()
                    .map(feature -> {

                        FeatureDto iDto = new FeatureDto();
                        iDto.setFeaSec(feature.getFeaSec());
                        iDto.setFeaName(feature.getFeaName());
                        iDto.setFeaEst(feature.getFeaEst());
                        iDto.setFeaIconUrl(feature.getFeaIconUrl());

                        return iDto;
                    })
                    .collect(Collectors.toList());

            dto.setFeatures(featureDto);
        }

        return dto;
    }

    @Override
    public HotelResponseDto findDetailById(Integer id) {

        Hotel hotel = repository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel no found with id: " + id));

        return hotelMapper.toDetailDto(hotel);
    }
}