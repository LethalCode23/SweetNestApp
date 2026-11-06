package com.dh.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelUnitDto {

    private Integer uniSec;

    @NotNull(message = "El hotel es obligatorio")
    private Integer hotSec;

    @NotBlank(message = "El nombre de la unidad es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
    private String uniName;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad minima es 1")
    private Integer uniCapacity;

    @NotNull(message = "El precio por noche es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Integer uniPrice;

    @Pattern(regexp = "[AI]", message = "uniState debe ser 'A' o 'I'")
    private String uniState;
}
