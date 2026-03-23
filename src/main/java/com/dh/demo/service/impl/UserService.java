package com.dh.demo.service.impl;

import com.dh.demo.authentication.LoginRequest;
import com.dh.demo.authentication.RegisterRequest;
import com.dh.demo.config.JwtService;
import com.dh.demo.dto.UserDto;
import com.dh.demo.entity.Role;
import com.dh.demo.entity.User;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public void save(RegisterRequest registerRequest) {

        User user = User.builder()
                .userFirstName(registerRequest.getFirstName())
                .userLastName(registerRequest.getLastName())
                .userEmail(registerRequest.getEmail())
                .userPass(passwordEncoder.encode(registerRequest.getPassword()))
                .userRole(Role.USER)
                .build();

        repository.save(user);
    }

    @Override
    public UserDto login(LoginRequest loginRequest) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User user = repository.findByUserEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        return UserDto.builder()
                .userSec(user.getUserSec())
                .userFirstName(user.getUserFirstName())
                .userLastName(user.getUserLastName())
                .userEmail(user.getUserEmail())
                .userRole(user.getUserRole())
                .userToken(token)
                .build();
    }

    @Override
    public Optional<UserDto> findByEmail(String userEmail) {
        return Optional.empty();
    }

    @Override
    public List<UserDto> findAll() {
        return null;
    }

    @Override
    public void delete(Long userSec) {

    }
}