package com.dh.demo.controller;

import com.dh.demo.config.i18n.MessageService;
import com.dh.demo.dto.ModuleDto;
import com.dh.demo.dto.request.UpdateEntryAllowedRequest;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.service.impl.RbacService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class RbacController {

    private final RbacService rbacService;
    private final MessageService messageService;

    @GetMapping("/{profileId}/modules")
    public ResponseEntity<List<ModuleDto>> getModulesWithActions(@PathVariable Long profileId) {
        return ResponseEntity.ok(rbacService.getModulesWithActionsByProfile(profileId));
    }

    @PatchMapping("/{profileId}/modules/{moduleId}/entry-allowed")
    public ResponseEntity<ApiResponse<Void>> updateEntryAllowed(
            @PathVariable Long profileId,
            @PathVariable Long moduleId,
            @Valid @RequestBody UpdateEntryAllowedRequest request) {

        rbacService.updateEntryAllowed(profileId, moduleId, request.getEntryAllowed());

        ApiResponse<Void> response = ApiResponse.success(
                messageService.getMessage("permission.entryAllowed.updated"),
                null
        );

        return ResponseEntity.ok(response);
    }
}