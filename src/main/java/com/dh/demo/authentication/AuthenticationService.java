package com.dh.demo.authentication;

import com.dh.demo.config.JwtService;
import com.dh.demo.entity.Profile;
import com.dh.demo.entity.User;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final IUserRepository iUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ProfileRepository profileRepository;

    public AuthenticationResponse register(RegisterRequest request) {

        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new IllegalArgumentException("profile no found - id: " + request.getProfileId()));

        var user = User.builder()
                .userFirstName(request.getFirstName())
                .userLastName(request.getLastName())
                .userEmail(request.getEmail())
                .userPass(passwordEncoder.encode(request.getPassword()))
                .profile(profile)
                .build();

        iUserRepository.save(user);

        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = iUserRepository.findByUserEmail(request.getEmail())
                .orElseThrow();

        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }
}