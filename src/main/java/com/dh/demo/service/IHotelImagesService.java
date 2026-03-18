package com.dh.demo.service;

import com.dh.demo.dto.HotelImagesDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IHotelImagesService {

    HotelImagesDto save(HotelImagesDto hotelImagesDto, MultipartFile file);
    void delete(Integer hotImgSec);

    List<HotelImagesDto> findAllByHotel(Integer hotSec);
}