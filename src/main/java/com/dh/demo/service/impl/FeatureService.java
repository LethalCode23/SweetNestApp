package com.dh.demo.service.impl;

import com.dh.demo.dto.FeatureDto;
import com.dh.demo.entity.Feature;
import com.dh.demo.mapper.FeatureMapper;
import com.dh.demo.repository.IFeatureRepository;
import com.dh.demo.service.IFeatureService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FeatureService implements IFeatureService {

    private final IFeatureRepository featureRepository;
    private final FeatureMapper featureMapper;

    @Override
    public FeatureDto save(FeatureDto featureDto) {

        Feature feature;

        if (featureDto.getFeaEst() == null) {
            featureDto.setFeaEst('A');
        }

        feature = featureMapper.toEntity(featureDto);
        featureRepository.save(feature);

        return featureDto;
    }

    @Override
    public Optional<FeatureDto> findById(Integer id) {

        return featureRepository.findById(id)
                .map(featureMapper::toDto);
    }

    @Override
    public FeatureDto update(Integer id, FeatureDto featureDto) {

        Feature existingFeature = featureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feature no encontrada con ID: " + id));

        existingFeature.setFeaName(featureDto.getFeaName());

        if (featureDto.getFeaEst() != null) {
            existingFeature.setFeaEst(featureDto.getFeaEst());
        }

        Feature updatedFeature = featureRepository.save(existingFeature);
        return featureMapper.toDto(updatedFeature);
    }

    @Override
    public void delete(Integer id) {

        if (!featureRepository.existsById(id)) {

            throw new EntityNotFoundException(
                    "No se puede eliminar. Feature no encontrada con ID: " + id);
        }

        featureRepository.deleteById(id);
    }

    @Override
    public List<FeatureDto> findAll() {

        return featureRepository.findAll()
                .stream()
                .map(featureMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeatureDto> findByAllId(List<Integer> featuresIds) {

        return featureRepository.findAllById(featuresIds)
                .stream()
                .map(featureMapper::toDto)
                .collect(Collectors.toList());
    }
}