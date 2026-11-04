package com.dh.demo.controller;

import com.dh.demo.dto.HotelDto;
import com.dh.demo.dto.HotelFilterDto;
import com.dh.demo.dto.HotelResponseDto;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.dto.response.HotelUnavailableDatesResponse;
import com.dh.demo.dto.response.SearchSuggestionDto;
import com.dh.demo.service.IHotelService;
import com.dh.demo.service.IReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/hotel")
public class HotelController {

    private final IHotelService service;
    private final IReservationService reservationService;

    public HotelController(IHotelService service, IReservationService reservationService) {
        this.service = service;
        this.reservationService = reservationService;
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

    /** Autocompletado del buscador: ciudades + hoteles (max 10). q < 2 chars -> []. */
    @GetMapping("/suggestions")
    public ResponseEntity<List<SearchSuggestionDto>> suggestions(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(service.getSuggestions(q));
    }

    @GetMapping("/findDetailById/{id}")
    public ResponseEntity<ApiResponse<HotelResponseDto>> findDetailById(@PathVariable Integer id) {

        HotelResponseDto hotel = service.findDetailById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Hotel Success", hotel)
        );
    }

    /**
     * Publico: dias sin ninguna unidad libre (reservas PENDING / CONFIRMED) para el
     * calendario del frontend. from default hoy, to (exclusivo) default from + 6 meses.
     */
    @GetMapping("/{hotSec}/unavailable-dates")
    public ResponseEntity<ApiResponse<HotelUnavailableDatesResponse>> unavailableDates(
            @PathVariable Integer hotSec,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(ApiResponse.success("Fechas no disponibles obtenidas",
                reservationService.findUnavailableDates(hotSec, from, to)));
    }
}