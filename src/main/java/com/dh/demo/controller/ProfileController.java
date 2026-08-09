package com.dh.demo.controller;

import com.dh.demo.dto.ProfileDto;
import com.dh.demo.service.IProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/profile")
public class ProfileController {

    private final IProfileService service;

    public ProfileController(IProfileService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<ProfileDto> save(@RequestBody ProfileDto profileDto) {

        ProfileDto saved = service.save(profileDto);
        return ResponseEntity.ok(saved);
    }
}