package com.dh.demo.controller;

import com.dh.demo.dto.HotelUnitDto;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.service.IHotelUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/hotel-unit")
@RequiredArgsConstructor
public class HotelUnitController {

    private final IHotelUnitService service;

    @GetMapping("/hotel/{hotSec}")
    public ResponseEntity<ApiResponse<List<HotelUnitDto>>> findByHotel(@PathVariable Integer hotSec) {
        return ResponseEntity.ok(ApiResponse.success("Unidades obtenidas", service.findByHotel(hotSec)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelUnitDto>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Unidad obtenida", service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HotelUnitDto>> create(@Valid @RequestBody HotelUnitDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Unidad creada", service.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelUnitDto>> update(@PathVariable Integer id,
                                                            @Valid @RequestBody HotelUnitDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Unidad actualizada", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Unidad eliminada", null));
    }
}
