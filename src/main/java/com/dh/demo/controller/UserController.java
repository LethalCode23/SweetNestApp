package com.dh.demo.controller;

import com.dh.demo.config.i18n.MessageService;
import com.dh.demo.dto.UserDto;
import com.dh.demo.dto.request.UpdateUserRequest;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final MessageService messageService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserDto>>> findAll() {

        ApiResponse<List<UserDto>> response = ApiResponse.success(
                messageService.getMessage("user.found.all"),
                userService.findAll()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userSec}")
    public ResponseEntity<ApiResponse<UserDto>> findById(@PathVariable Long userSec) {

        UserDto userDto = userService.findById(userSec)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageService.getMessage("user.no.found")));

        ApiResponse<UserDto> response = ApiResponse.success(
                messageService.getMessage("user.found"),
                userDto
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<ApiResponse<UserDto>> findByEmail(@PathVariable String email) {

        UserDto userDto = userService.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageService.getMessage("user.no.found")));

        ApiResponse<UserDto> response = ApiResponse.success(
                messageService.getMessage("user.found"),
                userDto
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userSec}")
    public ResponseEntity<ApiResponse<UserDto>> update(
            @PathVariable Long userSec,
            @Valid @RequestBody UpdateUserRequest request) {

        UserDto updated = userService.update(userSec, request);

        ApiResponse<UserDto> response = ApiResponse.success(
                messageService.getMessage("user.updated"),
                updated
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userSec}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long userSec) {

        userService.delete(userSec);

        ApiResponse<Void> response = ApiResponse.success(
                messageService.getMessage("user.deleted"),
                null
        );

        return ResponseEntity.ok(response);
    }
}