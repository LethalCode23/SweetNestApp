package com.dh.demo.authentication;

import com.dh.demo.config.i18n.MessageService;
import com.dh.demo.dto.UserDto;
import com.dh.demo.service.impl.UserService;
import jakarta.validation.Valid;
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
    private final MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

        userService.save(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageService.getMessage("user.register.success"));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest request) {

        UserDto userAuthenticated = userService.login(request);
        return ResponseEntity
                .ok(userAuthenticated);
    }
}