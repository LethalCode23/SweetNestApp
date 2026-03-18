package com.dh.demo.service.impl;

import com.dh.demo.dto.HotelDto;
import com.dh.demo.entity.City;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelImages;
import com.dh.demo.repository.CityRepository;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.service.IHotelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService implements IHotelService {

    private final HotelRepository repository;
    private final CityRepository cityRepository;

    public HotelService(HotelRepository repository, CityRepository cityRepository) {
        this.repository = repository;
        this.cityRepository = cityRepository;
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

        hotel.setCity(city);
        Hotel savedHotel = repository.save(hotel);

        return new HotelDto(
                savedHotel.getHotSec(),
                savedHotel.getHotName(),
                savedHotel.getHotDescription(),
                savedHotel.getHotAddress(),
                savedHotel.getHotCost(),
                savedHotel.getHotState(),
                savedHotel.getCity().getCitSec()
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
                        hotel.getCity() != null ? hotel.getCity().getCitSec() : null
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
    public Page<HotelDto> findByHotel(String citName, Pageable pageable) {

        Page<Hotel> hotels = repository
                .findByCity_CitNameContainingIgnoreCase(citName, pageable);

        return hotels.map(this::mapToDto);
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

        if (hotel.getHotelImages() != null && !hotel.getHotelImages().isEmpty()) {

            List<String> imageUrls = hotel.getHotelImages().stream()
                    .map(HotelImages::getHotImgUrl)
                    .collect(Collectors.toList());
            dto.setImageUrls(imageUrls);
        }

        return dto;
    }
}