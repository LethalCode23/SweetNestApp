package com.dh.demo.controller;

import com.dh.demo.dto.ModuleDto;
import com.dh.demo.service.IModuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/module")
public class ModuleController {

    private final IModuleService service;

    public ModuleController(IModuleService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<ModuleDto> save(@RequestBody ModuleDto moduleDto) {

        ModuleDto saved = service.save(moduleDto);
        return ResponseEntity.ok(saved);
    }
}