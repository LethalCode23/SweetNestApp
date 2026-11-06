package com.dh.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelTypeDto {

    private Integer typSec;

    @NotBlank(message = "El nombre del tipo es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    private String typName;

    /** 'S' permite crear varias unidades, 'N' el hotel tiene una sola unidad automatica. */
    @NotBlank(message = "El indicador multi-unidad es obligatorio")
    @Pattern(regexp = "[SN]", message = "typMultiUnit debe ser 'S' o 'N'")
    private String typMultiUnit;

    @Pattern(regexp = "[AI]", message = "typState debe ser 'A' o 'I'")
    private String typState;
}
