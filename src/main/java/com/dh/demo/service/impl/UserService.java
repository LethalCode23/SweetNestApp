package com.dh.demo.service.impl;

import com.dh.demo.authentication.LoginRequest;
import com.dh.demo.authentication.RegisterRequest;
import com.dh.demo.config.JwtService;
import com.dh.demo.dto.AuthDto;
import com.dh.demo.dto.ProfileDto;
import com.dh.demo.dto.RegistrationEmailDTO;
import com.dh.demo.dto.UserDto;
import com.dh.demo.dto.request.UpdateUserRequest;
import com.dh.demo.dto.response.LoginResponseDto;
import com.dh.demo.dto.response.RegisterResponseDto;
import com.dh.demo.entity.Profile;
import com.dh.demo.entity.User;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.service.IEmailService;
import com.dh.demo.service.IProfileService;
import com.dh.demo.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IProfileService profileService;
    private final IEmailService emailService;

    @Value("${app.frontend.login-url}")
    private String loginUrl;

    @Override
    public RegisterResponseDto save(RegisterRequest registerRequest) {

        ProfileDto profile;

        if (registerRequest.getProfileId() == null){

            profile = profileService.findByIsDefault('S') // S for default
                    .orElseThrow(() -> new IllegalArgumentException("Profile no found"));
        } else {

            profile = profileService.findById(registerRequest.getProfileId())
                    .orElseThrow(() -> new IllegalArgumentException("Profile no found"));
        }

        Profile profileSave = new Profile( // pendiente ajustar
                profile.getProId(),
                profile.getProName(),
                profile.getProState(),
                profile.getIsDefault(),
                profile.getHasControlAccess()
        );

        User user = User.builder()
                .userFirstName(registerRequest.getFirstName())
                .userLastName(registerRequest.getLastName())
                .userEmail(registerRequest.getEmail())
                .userPass(passwordEncoder.encode(registerRequest.getPassword()))
                .profile(profileSave)
                .build();

        User savedUser = repository.save(user);

        emailService.sendRegistrationConfirmation(
                RegistrationEmailDTO.builder()
                        .to(savedUser.getUserEmail())
                        .username(savedUser.getUserFirstName() + " " + savedUser.getUserLastName())
                        .loginUrl(loginUrl)
                        .build()
        );

        UserDto userDto = new UserDto(
                savedUser.getUserSec(),
                savedUser.getUserFirstName(),
                savedUser.getUserLastName(),
                savedUser.getUserEmail(),
                mapToDto(profileSave)
        );

        return new RegisterResponseDto(userDto);
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

        ProfileDto profileSearched = profileService.findById(user.getProfile().getId())
                .orElseThrow(() -> new IllegalArgumentException("Profile no found"));

        Profile profile = new Profile( // pendiente ajustar
                profileSearched.getProId(),
                profileSearched.getProName(),
                profileSearched.getProState(),
                profileSearched.getIsDefault(),
                profileSearched.getHasControlAccess()
        );

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

        return repository.findByUserEmail(userEmail)
                .map(this::mapToUserDto);
    }

    @Override
    public List<UserDto> findAll() {

        return repository.findAll()
                .stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userSec) {

        if (!repository.existsById(userSec)) {
            throw new EntityNotFoundException("User not found with id: " + userSec);
        }

        repository.deleteById(userSec);
    }

    @Override
    public Optional<UserDto> findById(Long userSec) {
        return repository.findById(userSec)
                .map(this::mapToUserDto);
    }

    @Override
    public UserDto update(Long userSec, UpdateUserRequest request) {

        User user = repository.findById(userSec)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userSec));

        ProfileDto profileSearched = profileService.findById(request.getProfileId())
                .orElseThrow(() -> new IllegalArgumentException("Profile no found"));

        Profile profile = new Profile( // pendiente ajustar
                profileSearched.getProId(),
                profileSearched.getProName(),
                profileSearched.getProState(),
                profileSearched.getIsDefault(),
                profileSearched.getHasControlAccess()
        );

        user.setUserFirstName(request.getFirstName());
        user.setUserLastName(request.getLastName());
        user.setUserEmail(request.getEmail());
        user.setProfile(profile);

        repository.save(user);

        return mapToUserDto(user);
    }

    private UserDto mapToUserDto(User user) {

        UserDto dto = UserDto.builder()
                .userSec(user.getUserSec())
                .firstName(user.getUserFirstName())
                .lastName(user.getUserLastName())
                .email(user.getUserEmail())
                .build();

        if (user.getProfile() != null) {
            dto.setProfile(mapToDto(user.getProfile()));
        }

        return dto;
    }

    private ProfileDto mapToDto(Profile profile) {

        ProfileDto dto = new ProfileDto();
        dto.setProId(profile.getId());
        dto.setProName(profile.getName());
        dto.setProState(profile.getState());
        dto.setIsDefault(profile.getIsDefault());
        dto.setHasControlAccess(profile.getHasControlAccess());

        return dto;
    }
}