package com.dh.demo.repository;

import com.dh.demo.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFeatureRepository extends JpaRepository<Feature, Integer> { }