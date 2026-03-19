package com.dh.demo.authentication;

import com.dh.demo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private Long userSec;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userToken;
    private Role userRole;
    private String token;
}