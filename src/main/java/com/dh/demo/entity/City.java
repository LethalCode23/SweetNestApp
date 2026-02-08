package com.dh.demo.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "citSec")
    private Integer citSec;

    @Column(name = "citName")
    private String citName;

    @ManyToOne
    @JoinColumn(name = "citDepSec", nullable = false)
    private Department department;

    @Column(name = "citState")
    private Character citState;

    @OneToMany(mappedBy = "city")
    private List<Hotel> hotels;

    public City() {
    }

    public Integer getCitSec() {
        return citSec;
    }

    public void setCitSec(Integer citSec) {
        this.citSec = citSec;
    }

    public String getCitName() {
        return citName;
    }

    public void setCitName(String citName) {
        this.citName = citName;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Character getCitState() {
        return citState;
    }

    public void setCitState(Character citState) {
        this.citState = citState;
    }
}