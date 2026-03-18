package com.dh.demo.service.impl;

import com.dh.demo.dto.HotelImagesDto;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelImages;
import com.dh.demo.repository.HotelImagesRepository;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.service.IHotelImagesService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HotelImagesServiceService implements IHotelImagesService {

    private final HotelImagesRepository repository;
    private final HotelRepository hotelRepository;

    public HotelImagesServiceService(HotelImagesRepository repository, HotelRepository hotelRepository) {
        this.repository = repository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public HotelImagesDto save(HotelImagesDto hotelImagesDto, String hotelImageUrl) {

        HotelImages hotelImages = new HotelImages();
        Optional<Hotel> hotel = hotelRepository.findById(hotelImagesDto.getHotSec());

        if (hotel.isEmpty()) {
            throw new RuntimeException("Hotel not found");
        }

        hotelImages.setHotel(hotel.get());
        hotelImages.setHotImgUrl(hotelImageUrl);
        hotelImages.setHotImgPri(hotelImagesDto.getHotImgPri());
        HotelImages hotelImagesSave = repository.save(hotelImages);

        return new HotelImagesDto(hotelImagesSave.getHotImgSec(),
                hotelImagesDto.getHotSec(),
                hotelImages.getHotImgPri());
    }

    @Override
    public void delete(Integer hotImgSec) {
        this.repository.deleteById(hotImgSec);
    }

    @Override
    public List<HotelImagesDto> findAllByHotel(Integer hotSec) {
        return null;
    }
}