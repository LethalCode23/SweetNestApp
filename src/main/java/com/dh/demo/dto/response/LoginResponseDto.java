package com.dh.demo.dto.response;

import com.dh.demo.dto.AuthDto;
import com.dh.demo.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private UserDto user;
    private AuthDto auth;
}