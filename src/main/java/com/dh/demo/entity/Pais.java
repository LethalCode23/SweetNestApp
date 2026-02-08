package com.dh.demo.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pais")
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paiSec")
    private Integer paiSec;

    @Column(name = "paiName")
    private String paiName;

    @Column(name = "paiState")
    private Character paiState;

    @OneToMany(mappedBy = "pais")
    private List<Department> departments;

    public Pais() { }

    public Integer getPaiSec() {
        return paiSec;
    }

    public void setPaiSec(Integer paiSec) {
        this.paiSec = paiSec;
    }

    public String getPaiName() {
        return paiName;
    }

    public void setPaiName(String paiName) {
        this.paiName = paiName;
    }

    public Character getPaiState() {
        return paiState;
    }

    public void setPaiState(Character paiState) {
        this.paiState = paiState;
    }
}