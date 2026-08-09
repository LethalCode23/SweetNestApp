package com.dh.demo.service.impl;

import com.dh.demo.dto.CategoryDto;
import com.dh.demo.dto.HotelDto;
import com.dh.demo.dto.HotelFilterDto;
import com.dh.demo.dto.HotelImagesDto;
import com.dh.demo.entity.Category;
import com.dh.demo.entity.City;
import com.dh.demo.entity.Hotel;
import com.dh.demo.repository.CategoryRepository;
import com.dh.demo.repository.CityRepository;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.service.IHotelService;
import com.dh.demo.specification.HotelSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService implements IHotelService {

    private final HotelRepository repository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;

    public HotelService(HotelRepository repository, CityRepository cityRepository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.cityRepository = cityRepository;
        this.categoryRepository = categoryRepository;
    }

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

        // Page<Hotel> hotels = repository.findByCity_CitNameContainingIgnoreCase(citName, pageable);

        Specification<Hotel> specification = HotelSpecification.withFilters(hotelFilterDto);
        // return hotels.map(this::mapToDto);
        return repository.findAll(specification, pageable).map(this::mapToDto);
    }

    private static Hotel getHotel(HotelDto hotelDto) {

        Hotel hotel = new Hotel();

        /* city */
        City city = new City();
        city.setCitSec(hotelDto.getHotCitSec());

        /* appointment */
        hotel.setHotSec(hotelDto.getHotSec());
        hotel.setCity(city);

        return hotel;
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

            /*List<String> imageUrls = hotel.getHotelImages().stream()
                    .map(HotelImages::getHotImgUrl)
                    .collect(Collectors.toList());
            dto.setImageUrls(imageUrls);*/

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

        return dto;
    }
}