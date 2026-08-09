package com.dh.demo.service.impl;

import com.dh.demo.authentication.LoginRequest;
import com.dh.demo.authentication.RegisterRequest;
import com.dh.demo.config.JwtService;
import com.dh.demo.dto.AuthDto;
import com.dh.demo.dto.ProfileDto;
import com.dh.demo.dto.UserDto;
import com.dh.demo.dto.response.LoginResponseDto;
import com.dh.demo.entity.Profile;
import com.dh.demo.entity.User;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.repository.ProfileRepository;
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
    private final ProfileRepository profileRepository;

    @Override
    public void save(RegisterRequest registerRequest) {

        Profile profile = profileRepository.findById(registerRequest.getProfileId())
                .orElseThrow(() -> new IllegalArgumentException("Profile no found"));

        User user = User.builder()
                .userFirstName(registerRequest.getFirstName())
                .userLastName(registerRequest.getLastName())
                .userEmail(registerRequest.getEmail())
                .userPass(passwordEncoder.encode(registerRequest.getPassword()))
                .profile(profile)
                .build();

        repository.save(user);
    }

    @Override
    public LoginResponseDto login(LoginRequest loginRequest) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User user = repository.findByUserEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Profile profile = profileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new IllegalArgumentException("Profile no found"));

        String token = jwtService.generateToken(user);

        UserDto userDto = UserDto.builder()
                .firstName(user.getUserFirstName())
                .lastName(user.getUserLastName())
                .email(user.getUserEmail())
                .profile(mapToDto(profile))
                .build();

        AuthDto authDto = AuthDto.builder()
                .token(token)
                .tokenType("Bearer")
                .build();

        return LoginResponseDto.builder()
                .user(userDto)
                .auth(authDto)
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

    private ProfileDto mapToDto(Profile profile) {

        ProfileDto dto = new ProfileDto();
        dto.setProId(profile.getId());
        dto.setProName(profile.getName());
        dto.setProState(profile.getState());

        return dto;
    }
}