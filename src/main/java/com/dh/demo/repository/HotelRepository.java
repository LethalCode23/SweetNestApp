package com.dh.demo.repository;

import com.dh.demo.dto.response.SearchSuggestionDto;
import com.dh.demo.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer>, JpaSpecificationExecutor<Hotel> {

    // Page<Hotel> findByCity_CitNameContainingIgnoreCase(String name, Pageable pageable);

    /** Hoteles activos cuyo nombre contiene el termino (sin distinguir mayusculas). Solo id + nombre. */
    @Query("select new com.dh.demo.dto.response.SearchSuggestionDto('HOTEL', h.hotSec, h.hotName) " +
           "from Hotel h " +
           "where lower(h.hotName) like lower(concat('%', :q, '%')) and h.hotState = 'A' " +
           "order by h.hotName")
    List<SearchSuggestionDto> findSuggestions(@Param("q") String q, Pageable pageable);
}
