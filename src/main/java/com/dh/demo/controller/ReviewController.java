package com.dh.demo.controller;

import com.dh.demo.config.i18n.MessageService;
import com.dh.demo.dto.request.ReviewRequest;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.dto.response.HotelReviewsResponse;
import com.dh.demo.dto.response.MyReviewResponse;
import com.dh.demo.service.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * HU #28: valoraciones de hoteles. El listado es publico (SecurityConfiguration);
 * /mine y PUT exigen sesion y el usuario sale del token. Mensajes: claves review.*.
 */
@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService service;
    private final MessageService messageService;

    @GetMapping("/hotel/{hotSec}")
    public ResponseEntity<ApiResponse<HotelReviewsResponse>> findByHotel(
            @PathVariable Integer hotSec,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(ApiResponse.success(messageService.getMessage("review.found.all"),
                service.findByHotel(hotSec, PageRequest.of(page, size))));
    }

    @GetMapping("/hotel/{hotSec}/mine")
    public ResponseEntity<ApiResponse<MyReviewResponse>> findMine(
            @PathVariable Integer hotSec,
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.ok(ApiResponse.success(messageService.getMessage("review.mine.found"),
                service.findMine(hotSec, principal.getUsername())));
    }

    @PutMapping("/hotel/{hotSec}")
    public ResponseEntity<ApiResponse<HotelReviewsResponse>> upsert(
            @PathVariable Integer hotSec,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.ok(ApiResponse.success(messageService.getMessage("review.saved"),
                service.upsert(hotSec, principal.getUsername(), request)));
    }
}
