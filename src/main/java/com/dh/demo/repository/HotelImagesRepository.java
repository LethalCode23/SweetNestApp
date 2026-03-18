package com.dh.demo.repository;

import com.dh.demo.entity.HotelImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelImagesRepository extends JpaRepository<HotelImages, Integer> { }