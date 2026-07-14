package com.dh.demo.service.impl;

import com.dh.demo.dto.HotelImagesDto;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelImages;
import com.dh.demo.repository.HotelImagesRepository;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.service.IFileStorageService;
import com.dh.demo.service.IHotelImagesService;
import com.dh.demo.validator.FileValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Service
public class HotelImagesService implements IHotelImagesService {

    private final HotelImagesRepository repository;
    private final HotelRepository hotelRepository;
    private final IFileStorageService fileStorageService;
    private final FileValidator fileValidator;

    public HotelImagesService(HotelImagesRepository repository, HotelRepository hotelRepository,
                    IFileStorageService fileStorageService,
                              FileValidator fileValidator) {

        this.repository = repository;
        this.hotelRepository = hotelRepository;
        this.fileStorageService = fileStorageService;
        this.fileValidator = fileValidator;
    }

    @Override
    public HotelImagesDto save(HotelImagesDto hotelImagesDto, MultipartFile file) {

        fileValidator.validate(file);
        String fileUrl = fileStorageService.store(file, "hotel-images");

        try {

            Optional<Hotel> hotel = hotelRepository.findById(hotelImagesDto.getHotSec());

            if (hotel.isEmpty()) {
                throw new RuntimeException("Hotel not found");
            }

            HotelImages hotelImages = new HotelImages();
            hotelImages.setHotel(hotel.get());
            hotelImages.setHotImgUrl(fileUrl);
            hotelImages.setHotImgPri(hotelImagesDto.getHotImgPri());

            // save
            HotelImages hotelImagesSave = repository.save(hotelImages);

            return new HotelImagesDto(hotelImagesSave.getHotImgSec(),
                    hotelImagesDto.getHotSec(),
                    fileUrl,
                    hotelImages.getHotImgPri());

        } catch (Exception e) {

            fileStorageService.delete(fileUrl);
            throw e;
        }
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