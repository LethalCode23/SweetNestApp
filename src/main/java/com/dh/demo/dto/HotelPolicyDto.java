package com.dh.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelPolicyDto {

    /** Solo informativo en la respuesta; al guardar se reemplaza la lista completa. */
    private Integer polSec;

    @NotBlank(message = "El titulo de la politica es obligatorio")
    @Size(max = 100, message = "El titulo de la politica admite maximo 100 caracteres")
    private String polTitle;

    @NotBlank(message = "La descripcion de la politica es obligatoria")
    @Size(max = 1000, message = "La descripcion de la politica admite maximo 1000 caracteres")
    private String polDescription;
}
