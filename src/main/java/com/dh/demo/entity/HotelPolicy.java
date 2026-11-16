package com.dh.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Politica de uso de un alojamiento (cuidados y precauciones).
 * Se muestra en el bloque "Políticas" del detalle del hotel (HU #26).
 */
@Entity
@Table(name = "hotel_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "polSec")
    private Integer polSec;

    @Column(name = "polTitle", nullable = false)
    private String polTitle;

    @Column(name = "polDescription", nullable = false, length = 1000)
    private String polDescription;

    /** Orden de presentacion en el detalle (ascendente). */
    @Column(name = "polOrder")
    private int polOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polHotSec", nullable = false)
    private Hotel hotel;
}
