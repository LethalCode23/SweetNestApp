package com.dh.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * Unidad reservable de un alojamiento. Es lo unico que se reserva.
 * En hoteles multi-unidad: una habitacion. En casas/apartamentos: la unidad unica.
 */
@Entity
@Table(name = "hotel_units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uniSec")
    private Integer uniSec;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "uniHotSec", nullable = false)
    private Hotel hotel;

    @Column(name = "uniName", nullable = false)
    private String uniName;

    @Column(name = "uniCapacity", nullable = false)
    private Integer uniCapacity;

    /** Fuente de verdad del precio. */
    @Column(name = "uniPrice", nullable = false)
    private Integer uniPrice;

    @Column(name = "uniState")
    private Character uniState;

    /** Sin cascade: borrar una unidad con reservas falla y se responde 409. */
    @OneToMany(mappedBy = "unit")
    private List<Reservation> reservations = new ArrayList<>();
}
