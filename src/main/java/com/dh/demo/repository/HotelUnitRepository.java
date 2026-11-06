package com.dh.demo.repository;

import com.dh.demo.entity.HotelUnit;
import com.dh.demo.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelUnitRepository extends JpaRepository<HotelUnit, Integer> {

    List<HotelUnit> findByHotelHotSecAndUniStateOrderByUniNameAsc(Integer hotSec, Character uniState);

    /** true si la unidad tiene reservas que ocupan (PENDING / CONFIRMED). */
    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Reservation r
            WHERE r.unit.uniSec = :unitId
              AND r.resStatus IN :occupying
            """)
    boolean hasActiveReservations(@Param("unitId") Integer unitId,
                                  @Param("occupying") List<ReservationStatus> occupying);
}
