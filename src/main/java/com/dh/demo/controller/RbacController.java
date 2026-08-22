package com.dh.demo.controller;

import com.dh.demo.dto.ModuleDto;
import com.dh.demo.service.impl.RbacService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class RbacController {

    private final RbacService rbacService;

    @GetMapping("/{profileId}/modules")
    public ResponseEntity<List<ModuleDto>> getModulesWithActions(@PathVariable Long profileId) {
        return ResponseEntity.ok(rbacService.getModulesWithActionsByProfile(profileId));
    }
}