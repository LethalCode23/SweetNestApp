package com.dh.demo.repository;

import com.dh.demo.entity.HotelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelTypeRepository extends JpaRepository<HotelType, Integer> {

    List<HotelType> findByTypStateOrderByTypNameAsc(Character typState);
}
