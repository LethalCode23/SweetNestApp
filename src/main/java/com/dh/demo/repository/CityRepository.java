package com.dh.demo.repository;

import com.dh.demo.dto.response.SearchSuggestionDto;
import com.dh.demo.entity.City;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    /** Ciudades activas cuyo nombre contiene el termino (sin distinguir mayusculas). */
    @Query("select new com.dh.demo.dto.response.SearchSuggestionDto('CITY', c.citSec, c.citName) " +
           "from City c " +
           "where lower(c.citName) like lower(concat('%', :q, '%')) and c.citState = 'A' " +
           "order by c.citName")
    List<SearchSuggestionDto> findSuggestions(@Param("q") String q, Pageable pageable);
}
