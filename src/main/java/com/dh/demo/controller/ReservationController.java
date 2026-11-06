package com.dh.demo.controller;

import com.dh.demo.dto.request.CreateReservationRequest;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.dto.response.ReservationResponse;
import com.dh.demo.entity.enums.ReservationStatus;
import com.dh.demo.service.IReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService service;

    /** El usuario sale del token, nunca del body. */
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reserva registrada", service.create(request, principal.getUsername())));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> findMine(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success("Reservas obtenidas",
                service.findMine(principal.getUsername(), status, PageRequest.of(page, size))));
    }

    /** Bandeja del anfitrion: reservas de SUS hoteles (Fase 2). */
    @GetMapping("/incoming")
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> findIncoming(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success("Reservas entrantes obtenidas",
                service.findIncoming(principal.getUsername(), status, PageRequest.of(page, size))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> findAll(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success("Reservas obtenidas",
                service.findAll(status, PageRequest.of(page, size))));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirm(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.ok(ApiResponse.success("Reserva confirmada",
                service.confirm(id, principal.getUsername())));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancel(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.ok(ApiResponse.success("Reserva cancelada",
                service.cancel(id, principal.getUsername())));
    }
}
