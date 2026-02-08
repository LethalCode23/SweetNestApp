package com.dh.demo.controller;

import com.dh.demo.dto.PaisDto;
import com.dh.demo.service.ICountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/country")
public class PaisController {

    private final ICountryService service;

    public PaisController(ICountryService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<PaisDto> save(@RequestBody PaisDto paisDto) {

        PaisDto saved = service.save(paisDto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaisDto>> getCountries() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<PaisDto> findById(@PathVariable Integer id) {

        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PaisDto> update(@PathVariable Integer id,
                                          @RequestBody PaisDto paisDto) {

        PaisDto updated = service.update(id, paisDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}