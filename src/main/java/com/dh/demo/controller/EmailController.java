package com.dh.demo.controller;

import com.dh.demo.dto.request.EmailRequestDTO;
import com.dh.demo.dto.response.ApiResponse;
import com.dh.demo.service.IEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final IEmailService emailService;

    @PostMapping("send-simple-email")
    public ResponseEntity<ApiResponse<Void>> sendEmail(@Valid @RequestBody EmailRequestDTO request) {

        emailService.sendHtmlEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Correo enviado", null));
    }
}