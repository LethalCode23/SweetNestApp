package com.dh.demo.entity.enums;

/**
 * Estados de una reserva.
 *
 * Ocupan la unidad: PENDING y CONFIRMED.
 * No ocupan la unidad: CANCELLED y COMPLETED.
 */
public enum ReservationStatus {

    PENDING,    // nace aqui cuando el hotel requiere confirmacion (hotConfirmReq = 'S')
    CONFIRMED,  // reserva inmediata (hotConfirmReq = 'N') o aprobada por el administrador
    CANCELLED,
    COMPLETED
}
