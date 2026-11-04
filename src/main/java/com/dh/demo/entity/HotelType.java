package com.dh.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo de tipos de alojamiento.
 * multiUnit = 'S' -> el formulario permite crear varias unidades.
 * multiUnit = 'N' -> el sistema crea una unica unidad automatica con el nombre del hotel.
 */
@Entity
@Table(name = "hotel_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "typSec")
    private Integer typSec;

    @Column(name = "typName", nullable = false)
    private String typName;

    @Column(name = "typMultiUnit", nullable = false)
    private Character typMultiUnit;

    @Column(name = "typState")
    private Character typState;

    /** Sin cascade: si el tipo tiene hoteles, el DELETE falla y se responde 409. */
    @OneToMany(mappedBy = "type")
    private List<Hotel> hotels = new ArrayList<>();
}
