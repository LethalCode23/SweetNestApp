package com.dh.demo.controller;

import com.dh.demo.dto.HotelImagesDto;
import com.dh.demo.exception.FileStorageException;
import com.dh.demo.exception.InvalidFileException;
import com.dh.demo.service.IHotelImagesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/hotel-images")
public class HotelImagesController {

    private final IHotelImagesService service;

    public HotelImagesController(IHotelImagesService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<HotelImagesDto> save(@RequestParam("hotSec") Integer hotSec,
                                               @RequestParam("hotImgPri") int hotImgPri,
                                               @RequestParam("file") MultipartFile file) {
        try {
            HotelImagesDto saved = service.save(new HotelImagesDto(null, hotSec, hotImgPri), file);
            return ResponseEntity.ok(saved);
        } catch (InvalidFileException e) {
            return ResponseEntity.badRequest().build();
        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}