package com.dh.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Feature")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FeaSec")
    private Integer FeaSec;

    @Column(name = "FeaName")
    private String FeaName;

    @Column(name = "FeaEst")
    private Character FeaEst;

    @Column(name = "FeaIcon")
    private String FeaIconUrl;

    @ManyToMany(mappedBy = "Features")
    private List<Hotel> Hotels = new ArrayList<>();
}