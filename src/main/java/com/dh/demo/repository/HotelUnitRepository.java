package com.dh.demo.repository;

import com.dh.demo.entity.HotelUnit;
import com.dh.demo.entity.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelUnitRepository extends JpaRepository<HotelUnit, Integer> {

    List<HotelUnit> findByHotelHotSecAndUniStateOrderByUniNameAsc(Integer hotSec, Character uniState);

    /**
     * Fase 2B: lee la unidad con SELECT ... FOR UPDATE. Hasta que termine la transaccion,
     * otra reserva de la MISMA unidad espera (evita la doble reserva simultanea).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM HotelUnit u WHERE u.uniSec = :id")
    Optional<HotelUnit> findByIdForUpdate(@Param("id") Integer id);

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
