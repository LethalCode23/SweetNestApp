package com.dh.demo.service;

import com.dh.demo.authentication.LoginRequest;
import com.dh.demo.authentication.RegisterRequest;
import com.dh.demo.dto.UserDto;
import com.dh.demo.dto.response.LoginResponseDto;
import com.dh.demo.dto.response.RegisterResponseDto;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    RegisterResponseDto save(RegisterRequest registerRequest);
    LoginResponseDto login(LoginRequest loginRequest);
    Optional<UserDto> findByEmail(String userEmail);
    List<UserDto> findAll();
    void delete(Long userSec);
}