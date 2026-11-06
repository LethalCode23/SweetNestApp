package com.dh.demo.entity;

import com.dh.demo.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reserva de una unidad para un rango de fechas.
 * El precio se congela al reservar (resPrice) y el total lo calcula el backend.
 */
@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resSec")
    private Integer resSec;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "resUserSec", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "resUniSec", nullable = false)
    private HotelUnit unit;

    @Column(name = "resCheckIn", nullable = false)
    private LocalDate resCheckIn;

    /** Debe ser posterior a resCheckIn. El dia de salida queda libre para otra reserva. */
    @Column(name = "resCheckOut", nullable = false)
    private LocalDate resCheckOut;

    @Column(name = "resGuests", nullable = false)
    private Integer resGuests;

    /** Copia congelada del precio por noche al momento de reservar. */
    @Column(name = "resPrice", nullable = false)
    private Integer resPrice;

    /** Calculado en el backend: noches x precio por noche. */
    @Column(name = "resTotal", nullable = false)
    private Integer resTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "resStatus", nullable = false)
    private ReservationStatus resStatus;

    @Column(name = "resCreatedAt")
    private LocalDateTime resCreatedAt;

    @PrePersist
    void onCreate() {
        if (resCreatedAt == null) {
            resCreatedAt = LocalDateTime.now();
        }
    }
}
