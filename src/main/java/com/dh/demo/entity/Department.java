package com.dh.demo.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "depSec")
    private Integer depSec;

    @Column(name = "depName")
    private String depName;

    @ManyToOne
    @JoinColumn(name = "depPaiSec", nullable = false)
    private Pais pais;

    @Column(name = "depState")
    private Character depState;

    @OneToMany(mappedBy = "department")
    private List<City> cities;

    public Department() { }

    public Integer getDepSec() {
        return depSec;
    }

    public void setDepSec(Integer depSec) {
        this.depSec = depSec;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public Character getDepState() {
        return depState;
    }

    public void setDepState(Character depState) {
        this.depState = depState;
    }
}