package com.dh.demo.controller;

import com.dh.demo.config.i18n.MessageService;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.service.impl.PermissionProfilesModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/PermissionProMod")
@RequiredArgsConstructor
public class PermissionProfilesModuleController {

    private final MessageService messageService;
    private final PermissionProfilesModuleService permissionProfilesModuleService;

    @PostMapping("/{profileId}/modules/{moduleId}/submodules/{subModuleId}")
    public ResponseEntity<ApiResponse<Void>> grantSubModuleAccess(
            @PathVariable Long profileId,
            @PathVariable Long moduleId,
            @PathVariable Long subModuleId) {

        permissionProfilesModuleService.grantSubModuleAccess(profileId, moduleId, subModuleId);

        ApiResponse<Void> response = ApiResponse.success(
                messageService.getMessage("permission.submodule.granted"),
                null
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{profileId}/modules/{moduleId}/submodules/{subModuleId}")
    public ResponseEntity<ApiResponse<Void>> revokeSubModuleAccess(
            @PathVariable Long profileId,
            @PathVariable Long moduleId,
            @PathVariable Long subModuleId) {

        permissionProfilesModuleService.revokeSubModuleAccess(profileId, moduleId, subModuleId);

        ApiResponse<Void> response = ApiResponse.success(
                messageService.getMessage("permission.submodule.revoked"),
                null
        );

        return ResponseEntity.ok(response);
    }
}