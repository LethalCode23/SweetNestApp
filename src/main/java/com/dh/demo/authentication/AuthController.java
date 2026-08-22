package com.dh.demo.authentication;

import com.dh.demo.config.i18n.MessageService;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.dto.response.LoginResponseDto;
import com.dh.demo.dto.response.RegisterResponseDto;
import com.dh.demo.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(@Valid @RequestBody RegisterRequest request) {

        RegisterResponseDto responseDto = userService.save(request);

        ApiResponse<RegisterResponseDto> apiResponse = ApiResponse.success(
                messageService.getMessage("user.register.success"),
                responseDto
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequest request) {

        LoginResponseDto userAuthenticated = userService.login(request);

        ApiResponse<LoginResponseDto> body = ApiResponse.success(
                messageService.getMessage("user.login.success"),
                userAuthenticated
        );

        return ResponseEntity.ok(body);
    }
}