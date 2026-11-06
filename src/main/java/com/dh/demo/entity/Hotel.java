package com.dh.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotSec")
    private Integer hotSec;

    @Column(name = "hotName")
    private String hotName;

    @Column(name = "hotDescription")
    private String hotDescription;

    @Column(name = "hotAddress")
    private String hotAddress;

    @Column(name = "hotCost")
    private int hotCost;

    @ManyToOne
    @JoinColumn(name = "hotCitSec", nullable = false)
    private City city;

    /** Tipo de alojamiento (Hotel, Casa, Apartamento). */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hotTypSec", nullable = false)
    private HotelType type;

    /**
     * Politica de reserva del hotel.
     * 'S' = la reserva requiere confirmacion (nace PENDING).
     * 'N' = reserva inmediata (nace CONFIRMED).
     */
    @Column(name = "hotConfirmReq")
    private Character hotConfirmReq;

    /** Anfitrion responsable del hotel. Null = aun sin asignar (Fase 2). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotUserSec")
    private User host;

    @Column(name = "hotState")
    private Character hotState;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelUnit> units = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelImages> HotelImages = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "hotel_category",
            joinColumns = @JoinColumn(name = "hotSec_fk", referencedColumnName = "hotSec"), // FK de Hotel
            inverseJoinColumns = @JoinColumn(name = "CatSec_fk", referencedColumnName = "CatSec") // FK de Category
    )
    private List<Category> Categories = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "hotel_feature",
            joinColumns = @JoinColumn(name = "hotSec_fk", referencedColumnName = "hotSec"), // FK de Hotel
            inverseJoinColumns = @JoinColumn(name = "FeaSec_fk", referencedColumnName = "FeaSec") // FK de Category
    )
    private List<Feature> Features = new ArrayList<>();
}