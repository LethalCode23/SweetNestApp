package com.dh.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.awt.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureDto {

    private Integer feaSec;
    private String feaName;
    private Character feaEst;
    private String feaIconUrl;
}