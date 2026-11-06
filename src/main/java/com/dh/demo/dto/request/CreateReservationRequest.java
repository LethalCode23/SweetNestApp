package com.dh.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Cuerpo de POST /api/reservations.
 * El usuario NUNCA viene en el body: sale del token.
 */
@Getter
@Setter
public class CreateReservationRequest {

    @NotNull(message = "La unidad es obligatoria")
    private Integer unitId;

    @NotNull(message = "La fecha de entrada es obligatoria")
    private LocalDate checkIn;

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDate checkOut;

    @NotNull(message = "La cantidad de huespedes es obligatoria")
    @Min(value = 1, message = "La cantidad de huespedes minima es 1")
    private Integer guests;
}
