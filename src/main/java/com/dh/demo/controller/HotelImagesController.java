package com.dh.demo.controller;

import com.dh.demo.dto.HotelImagesDto;
import com.dh.demo.service.IHotelImagesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/hotelImages")
public class HotelImagesController {

    private final IHotelImagesService service;

    public HotelImagesController(IHotelImagesService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<HotelImagesDto> save(@RequestParam("hotSec") Integer hotSec,
                                               @RequestParam("hotImgPri") int hotImgPri,
                                               @RequestParam("file") MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        String url = "/uploads/" + fileName;
        HotelImagesDto saved = service.save(new HotelImagesDto(null, hotSec, hotImgPri
        ), url);

        return ResponseEntity.ok(saved);
    }
}