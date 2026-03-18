package com.dh.demo.repository;

import com.dh.demo.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    Page<Hotel> findByCity_CitNameContainingIgnoreCase(String name, Pageable pageable);
}