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
    private String firstName;
    private String lastName;
    private String email;
    private ProfileDto profile;
    /** Marca de anfitrion: 'S' o 'N'. */
    private String host;
}