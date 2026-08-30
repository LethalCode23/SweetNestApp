package com.dh.demo.controller;

import com.dh.demo.dto.ProfileDto;
import com.dh.demo.dto.UserDto;
import com.dh.demo.service.IProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileDto> update(@PathVariable Long id, @RequestBody ProfileDto profileDto) {
        ProfileDto updated = service.update(id, profileDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProfileDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}