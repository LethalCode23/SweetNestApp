package com.dh.demo.repository;

import com.dh.demo.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    Optional<Favorite> findByUserUserSecAndHotelHotSec(Long userSec, Integer hotSec);

    Page<Favorite> findByUserUserSecOrderByFavCreatedAtDescFavSecDesc(Long userSec, Pageable pageable);

    /** Solo los ids de hotel: el frontend los usa para pintar los corazones. */
    @Query("SELECT f.hotel.hotSec FROM Favorite f WHERE f.user.userSec = :userSec")
    List<Integer> findHotelIdsByUser(@Param("userSec") Long userSec);

    long countByUserUserSec(Long userSec);
}
