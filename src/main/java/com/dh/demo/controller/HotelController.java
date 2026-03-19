package com.dh.demo.controller;

import com.dh.demo.dto.HotelDto;
import com.dh.demo.service.IHotelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/hotel")
public class HotelController {

    private final IHotelService service;

    public HotelController(IHotelService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<HotelDto> save(@RequestBody HotelDto hotelDto) {
        HotelDto saved = service.save(hotelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<HotelDto> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByHotel")
    public ResponseEntity<Page<HotelDto>> findByHotel(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "") String citName) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findByHotel(citName, pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<HotelDto>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<HotelDto> update(@PathVariable Integer id,
                                           @RequestBody HotelDto hotelDto) {
        HotelDto updated = service.update(id, hotelDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}