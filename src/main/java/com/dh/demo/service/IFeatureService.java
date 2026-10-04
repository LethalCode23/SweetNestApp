package com.dh.demo.service;

import com.dh.demo.dto.FeatureDto;
import java.util.List;
import java.util.Optional;

public interface IFeatureService {

    FeatureDto save(FeatureDto featureDto);

    Optional<FeatureDto> findById(Integer id);

    FeatureDto update(Integer id, FeatureDto featureDto);

    void delete(Integer id);

    List<FeatureDto> findAll();

    List<FeatureDto> findByAllId(List<Integer> featuresIds);
}