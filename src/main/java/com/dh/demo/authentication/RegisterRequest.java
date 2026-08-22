package com.dh.demo.authentication;

import com.dh.demo.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter // Genera los get
@Setter // Genera los set
@ToString // Para que el println se vea bien
@NoArgsConstructor // VITAL para Jackson
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "{validation.required}")
    private String firstName;

    @NotBlank(message = "{validation.required}")
    private String lastName;

    @Email
    private String email;

    @Size(min = 6)
    private String password;
    private Long profileId;
}