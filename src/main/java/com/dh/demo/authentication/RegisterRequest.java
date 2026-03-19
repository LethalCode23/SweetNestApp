package com.dh.demo.authentication;

import lombok.*;

@Getter // Genera los get
@Setter // Genera los set
@ToString // Para que el println se vea bien
@NoArgsConstructor // VITAL para Jackson
@AllArgsConstructor
@Builder
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
}