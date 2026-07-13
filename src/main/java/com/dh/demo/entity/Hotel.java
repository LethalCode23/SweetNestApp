package com.dh.demo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotel")
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

    @Column(name = "hotState")
    private Character hotState;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelImages> HotelImages = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "hotel_category",
            joinColumns = @JoinColumn(name = "hotSec_fk", referencedColumnName = "hotSec"), // FK de Hotel
            inverseJoinColumns = @JoinColumn(name = "CatSec_fk", referencedColumnName = "CatSec") // FK de Category
    )
    private List<Category> Categories = new ArrayList<>();

    public Hotel() {

    }

    public Integer getHotSec() {
        return hotSec;
    }

    public void setHotSec(Integer hotSec) {
        this.hotSec = hotSec;
    }

    public String getHotName() {
        return hotName;
    }

    public void setHotName(String hotName) {
        this.hotName = hotName;
    }

    public String getHotDescription() {
        return hotDescription;
    }

    public void setHotDescription(String hotDescription) {
        this.hotDescription = hotDescription;
    }

    public String getHotAddress() {
        return hotAddress;
    }

    public void setHotAddress(String hotAddress) {
        this.hotAddress = hotAddress;
    }

    public int getHotCost() {
        return hotCost;
    }

    public void setHotCost(int hotCost) {
        this.hotCost = hotCost;
    }

    public Character getHotState() {
        return hotState;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setHotState(Character hotState) {
        this.hotState = hotState;
    }

    public List<HotelImages> getHotelImages() {
        return HotelImages;
    }

    public void setHotelImages(List<HotelImages> hotelImages) {
        HotelImages = hotelImages;
    }

    public List<Category> getCategories() {
        return Categories;
    }

    public void setCategories(List<Category> categories) {
        this.Categories = categories;
    }
}