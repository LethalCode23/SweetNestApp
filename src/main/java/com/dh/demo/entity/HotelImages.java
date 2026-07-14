package com.dh.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hotelImages")
public class HotelImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotImgSec")
    private Integer hotImgSec;

    @ManyToOne()
    @JoinColumn(name = "hotImgHotSec", nullable = false)
    private Hotel hotel;

    @Column(name = "hotImgUrl")
    private String hotImgUrl;

    @Column(name = "hotImgPri")
    private int hotImgPri;

    public HotelImages() {

    }

    public HotelImages(Integer hotImgSec, Hotel hotel, String hotImgUrl, int hotImgPri) {
        this.hotImgSec = hotImgSec;
        this.hotel = hotel;
        this.hotImgUrl = hotImgUrl;
        this.hotImgPri = hotImgPri;
    }

    public Integer getHotImgSec() {
        return hotImgSec;
    }

    public void setHotImgSec(Integer hotImgSec) {
        this.hotImgSec = hotImgSec;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getHotImgUrl() {
        return hotImgUrl;
    }

    public void setHotImgUrl(String hotImgUrl) {
        this.hotImgUrl = hotImgUrl;
    }

    public int getHotImgPri() {
        return hotImgPri;
    }

    public void setHotImgPri(int hotImgPri) {
        this.hotImgPri = hotImgPri;
    }
}