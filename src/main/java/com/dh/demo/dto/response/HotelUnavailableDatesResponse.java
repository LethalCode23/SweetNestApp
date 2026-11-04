package com.dh.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

/**
 * Dias del hotel sin ninguna unidad activa libre, para marcarlos en el calendario.
 * Rango consultado: [from, to) (to exclusivo). El dia de salida de una reserva queda libre.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelUnavailableDatesResponse {

    private Integer hotSec;
    private LocalDate from;
    private LocalDate to;
    private Integer totalUnits;
    private List<LocalDate> unavailableDates;
}
