package com.dh.demo.service.impl;

import com.dh.demo.dto.HotelDto;
import com.dh.demo.entity.City;
import com.dh.demo.entity.Hotel;
import com.dh.demo.repository.CityRepository;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.service.IHotelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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
        hotel.setHotImgUrl(hotelDto.getHotImgUrl());

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
                savedHotel.getCity().getCitSec(),
                savedHotel.getHotImgUrl()
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
                        hotel.getHotImgUrl()
                ));
    }

    @Override
    public HotelDto update(Integer id, HotelDto hotelDto) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public Page<HotelDto> findAll(Pageable pageable) {

        Page<Hotel> hotels = repository.findAll(pageable);
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
        dto.setHotImgUrl(hotel.getHotImgUrl());

        dto.setHotCitSec(
                hotel.getCity() != null
                        ? hotel.getCity().getCitSec()
                        : null
        );

        return dto;
    }

}