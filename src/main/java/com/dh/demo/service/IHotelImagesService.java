package com.dh.demo.service;

import com.dh.demo.dto.HotelImagesDto;
import java.util.List;

public interface IHotelImagesService {

    HotelImagesDto save(HotelImagesDto hotelImagesDto, String hotelImageUrl);
    void delete(Integer hotImgSec);

    List<HotelImagesDto> findAllByHotel(Integer hotSec);
}