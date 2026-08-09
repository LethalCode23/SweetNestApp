package com.dh.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CatSec")
    private Integer CatSec;

    @Column(name = "CatName")
    private String CatName;

    @Column(name = "CatEst")
    private Character CatEst;

    @ManyToMany(mappedBy = "Categories")
    private List<Hotel> Hotels = new ArrayList<>();
}