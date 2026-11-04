package com.dh.demo.controller;

import com.dh.demo.config.i18n.MessageService;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.dto.response.FavoriteResponse;
import com.dh.demo.service.IFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Favoritos del usuario del token. Todo exige sesion (anyRequest().authenticated()).
 * PUT y DELETE son idempotentes. Mensajes: claves favorite.* de messages*.properties.
 */
@RestController
@RequestMapping("api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final IFavoriteService service;
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FavoriteResponse>>> findMine(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        return ResponseEntity.ok(ApiResponse.success(messageService.getMessage("favorite.found.all"),
                service.findMine(principal.getUsername(), PageRequest.of(page, size))));
    }

    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<List<Integer>>> findMyHotelIds(
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.ok(ApiResponse.success(messageService.getMessage("favorite.ids.found"),
                service.findMyHotelIds(principal.getUsername())));
    }

    @PutMapping("/{hotSec}")
    public ResponseEntity<ApiResponse<FavoriteResponse>> add(
            @PathVariable Integer hotSec,
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.ok(ApiResponse.success(messageService.getMessage("favorite.added"),
                service.add(hotSec, principal.getUsername())));
    }

    @DeleteMapping("/{hotSec}")
    public ResponseEntity<ApiResponse<Void>> remove(
            @PathVariable Integer hotSec,
            @AuthenticationPrincipal UserDetails principal) {

        service.remove(hotSec, principal.getUsername());
        return ResponseEntity.ok(ApiResponse.success(messageService.getMessage("favorite.removed"), null));
    }
}
