package com.dh.demo.mapper;

import com.dh.demo.dto.FeatureDto;
import com.dh.demo.entity.Feature;
import org.springframework.stereotype.Component;

@Component
public class FeatureMapper {

    public Feature toEntity(FeatureDto dto) {

        Feature feature = new Feature();

        feature.setFeaSec(dto.getFeaSec());
        feature.setFeaName(dto.getFeaName());
        feature.setFeaEst(dto.getFeaEst());
        feature.setFeaIconUrl(dto.getFeaIconUrl());

        return feature;
    }

    public FeatureDto toDto(Feature feature) {

        FeatureDto dto = new FeatureDto();

        dto.setFeaSec(feature.getFeaSec());
        dto.setFeaName(feature.getFeaName());
        dto.setFeaEst(feature.getFeaEst());
        dto.setFeaIconUrl(feature.getFeaIconUrl());

        return dto;
    }
}