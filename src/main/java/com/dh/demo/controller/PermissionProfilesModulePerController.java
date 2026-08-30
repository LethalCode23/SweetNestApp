package com.dh.demo.controller;

import com.dh.demo.config.i18n.MessageService;
import com.dh.demo.dto.request.UpdateActionAllowedRequest;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.service.impl.PermissionProfilesModulePerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/PermissionProModPer")
@RequiredArgsConstructor
public class PermissionProfilesModulePerController {

    private final MessageService messageService;
    private final PermissionProfilesModulePerService permissionProfilesModulePerService;

    @PatchMapping("/{profileId}/modules/{moduleId}/submodules/{subModuleId}/actions/{code}")
    public ResponseEntity<ApiResponse<Void>> updateActionAllowed(
            @PathVariable Long profileId,
            @PathVariable Long moduleId,
            @PathVariable Long subModuleId,
            @PathVariable Character code,
            @Valid @RequestBody UpdateActionAllowedRequest request) {

        permissionProfilesModulePerService.updateActionAllowed(
                profileId, moduleId, subModuleId, code, request.getAllowed()
        );

        ApiResponse<Void> response = ApiResponse.success(
                messageService.getMessage("permission.action.updated"),
                null
        );

        return ResponseEntity.ok(response);
    }
}