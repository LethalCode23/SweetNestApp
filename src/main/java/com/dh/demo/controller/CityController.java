package com.dh.demo.controller;

import com.dh.demo.dto.CityDto;
import com.dh.demo.service.ICityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/city")
public class CityController {

    private final ICityService service;

    public CityController(ICityService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<CityDto> save(@RequestBody CityDto cityDto) {
        CityDto saved = service.save(cityDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<CityDto> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<CityDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CityDto> update(@PathVariable Integer id,
                                          @RequestBody CityDto cityDto) {
        CityDto updated = service.update(id, cityDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}