package com.dh.demo.controller;

import com.dh.demo.dto.HotelTypeDto;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.service.IHotelTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/hotel-types")
@RequiredArgsConstructor
public class HotelTypeController {

    private final IHotelTypeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HotelTypeDto>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Tipos de alojamiento obtenidos", service.findAll()));
    }

    @GetMapping("/actives")
    public ResponseEntity<ApiResponse<List<HotelTypeDto>>> findActives() {
        return ResponseEntity.ok(ApiResponse.success("Tipos activos obtenidos", service.findActives()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelTypeDto>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Tipo de alojamiento obtenido", service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HotelTypeDto>> create(@Valid @RequestBody HotelTypeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tipo de alojamiento creado", service.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelTypeDto>> update(@PathVariable Integer id,
                                                            @Valid @RequestBody HotelTypeDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Tipo de alojamiento actualizado", service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Tipo de alojamiento eliminado", null));
    }
}
