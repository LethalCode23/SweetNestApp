package com.dh.demo.repository;

import com.dh.demo.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Optional<Review> findByUserUserSecAndHotelHotSec(Long userSec, Integer hotSec);

    Page<Review> findByHotelHotSecOrderByRevUpdatedAtDescRevSecDesc(Integer hotSec, Pageable pageable);

    /** [promedio, cantidad] de las valoraciones del hotel. Promedio null si no hay. */
    @Query("SELECT AVG(r.revStars), COUNT(r) FROM Review r WHERE r.hotel.hotSec = :hotSec")
    List<Object[]> findStatsByHotel(@Param("hotSec") Integer hotSec);

    /** Filas [estrellas, cantidad] solo de las estrellas que tienen valoraciones. */
    @Query("SELECT r.revStars, COUNT(r) FROM Review r WHERE r.hotel.hotSec = :hotSec GROUP BY r.revStars")
    List<Object[]> countByStars(@Param("hotSec") Integer hotSec);
}
