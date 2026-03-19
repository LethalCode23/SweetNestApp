package com.dh.demo.controller;

import com.dh.demo.authentication.LoginRequest;
import com.dh.demo.authentication.RegisterRequest;
import com.dh.demo.dto.UserDto;
import com.dh.demo.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        System.out.println("Datos recibidos: " + request.toString());
        if (request.getFirstName() == null || request.getLastName() == null
                || request.getEmail() == null || request.getPassword() == null) {

            return new ResponseEntity<>("Data is empty", HttpStatus.BAD_REQUEST);
        }

        userService.save(request);
        return new ResponseEntity<>("User successfully registered", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest request) {

        if (request.getEmail() == null && request.getPassword() == null) {

        }

        UserDto userAuthenticated = userService.login(request);
        return ResponseEntity.ok(userAuthenticated);
    }
}