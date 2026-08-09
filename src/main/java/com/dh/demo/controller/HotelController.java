package com.dh.demo.controller;

import com.dh.demo.dto.HotelDto;
import com.dh.demo.dto.HotelFilterDto;
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

    @PostMapping("/save") // insert record
    public ResponseEntity<HotelDto> save(@RequestBody HotelDto hotelDto) {
        HotelDto saved = service.save(hotelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/update/{id}") // update record
    public ResponseEntity<HotelDto> update(@PathVariable Integer id,
                                           @RequestBody HotelDto hotelDto) {
        HotelDto updated = service.update(id, hotelDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all") // get fetch
    public ResponseEntity<Page<HotelDto>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @DeleteMapping("/delete/{id}") // delete record
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findById/{id}") // search by id
    public ResponseEntity<HotelDto> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByHotel") // main rest for search hotels
    public ResponseEntity<Page<HotelDto>> findByHotel(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                                      @ModelAttribute HotelFilterDto hotelFilterDto) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findByHotel(hotelFilterDto, pageable));
    }
}