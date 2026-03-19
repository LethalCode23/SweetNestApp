package com.dh.demo.service;

import com.dh.demo.authentication.LoginRequest;
import com.dh.demo.authentication.RegisterRequest;
import com.dh.demo.dto.UserDto;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    void save(RegisterRequest registerRequest);
    UserDto login(LoginRequest loginRequest);
    Optional<UserDto> findByEmail(String userEmail);
    List<UserDto> findAll();
    void delete(Long userSec);
}