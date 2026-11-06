package com.dh.demo.service;

import com.dh.demo.dto.request.CreateReservationRequest;
import com.dh.demo.dto.response.HotelUnavailableDatesResponse;
import com.dh.demo.dto.response.ReservationResponse;
import com.dh.demo.entity.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface IReservationService {

    /**
     * Crea una reserva para el usuario autenticado.
     * Valida fechas, capacidad y estado de la unidad/hotel, congela el precio
     * y decide el estado inicial segun la politica del hotel (hotConfirmReq).
     */
    ReservationResponse create(CreateReservationRequest request, String userEmail);

    /** Reservas del usuario autenticado, paginadas. */
    Page<ReservationResponse> findMine(String userEmail, ReservationStatus status, Pageable pageable);

    /** Reservas de todos los usuarios (solo administrador). */
    Page<ReservationResponse> findAll(ReservationStatus status, Pageable pageable);

    /**
     * Bandeja /incoming (Fase 2): reservas de los hoteles donde el usuario
     * es anfitrion (hotUserSec). Si no es anfitrion de nada, pagina vacia.
     */
    Page<ReservationResponse> findIncoming(String userEmail, ReservationStatus status, Pageable pageable);

    /** PENDING -> CONFIRMED: administrador o anfitrion del hotel (409/1019 si el hotel no tiene anfitrion). */
    ReservationResponse confirm(Integer id, String userEmail);

    /** PENDING/CONFIRMED -> CANCELLED (dueño de la reserva o administrador). */
    ReservationResponse cancel(Integer id, String userEmail);

    /**
     * Calendario publico: dias de [from, to) en los que TODAS las unidades activas del
     * hotel estan ocupadas (PENDING / CONFIRMED). from default hoy, to default from + 6 meses.
     */
    HotelUnavailableDatesResponse findUnavailableDates(Integer hotSec, LocalDate from, LocalDate to);
}
