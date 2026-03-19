package com.dh.demo.dto;

import com.dh.demo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userSec;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPass;
    private String userToken;
    private Role userRole;
}