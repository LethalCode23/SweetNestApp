package com.dh.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    private Long profileId;

    /** Marca de anfitrion (Fase 2): 'S' o 'N'. Null = no cambia la marca. */
    @Pattern(regexp = "S|N", message = "host debe ser 'S' o 'N'")
    private String host;
}