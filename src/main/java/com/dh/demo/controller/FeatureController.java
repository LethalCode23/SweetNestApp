package com.dh.demo.controller;

import com.dh.demo.config.i18n.MessageService;
import com.dh.demo.dto.FeatureDto;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.service.IFeatureService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/feature")
@RequiredArgsConstructor
public class FeatureController {

    private final IFeatureService featureService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<FeatureDto>> save(@Valid @RequestBody FeatureDto featureDto) {

        FeatureDto saved = featureService.save(featureDto);

        ApiResponse<FeatureDto> response = ApiResponse.success(
                messageService.getMessage("feature.created"),
                saved
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<FeatureDto>>> findAll() {

        ApiResponse<List<FeatureDto>> response = ApiResponse.success(
                messageService.getMessage("feature.found.all"),
                featureService.findAll()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeatureDto>> findById(@PathVariable Integer id) {

        FeatureDto featureDto = featureService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageService.getMessage("feature.no.found")));

        ApiResponse<FeatureDto> response = ApiResponse.success(
                messageService.getMessage("feature.found"),
                featureDto
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FeatureDto>> update(
            @PathVariable Integer id,
            @Valid @RequestBody FeatureDto featureDto) {

        FeatureDto updated = featureService.update(id, featureDto);

        ApiResponse<FeatureDto> response = ApiResponse.success(
                messageService.getMessage("feature.updated"),
                updated
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {

        featureService.delete(id);

        ApiResponse<Void> response = ApiResponse.success(
                messageService.getMessage("feature.deleted"),
                null
        );

        return ResponseEntity.ok(response);
    }
}