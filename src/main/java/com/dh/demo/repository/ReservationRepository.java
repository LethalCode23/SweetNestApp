package com.dh.demo.repository;

import com.dh.demo.entity.Reservation;
import com.dh.demo.entity.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    Page<Reservation> findByUserUserSecOrderByResCreatedAtDesc(Long userSec, Pageable pageable);

    Page<Reservation> findByUserUserSecAndResStatusOrderByResCreatedAtDesc(
            Long userSec, ReservationStatus status, Pageable pageable);

    // ── Fase 2: bandeja /incoming (reservas de los hoteles donde soy anfitrion) ──
    Page<Reservation> findByUnitHotelHostUserSecOrderByResCreatedAtDesc(Long hostSec, Pageable pageable);

    Page<Reservation> findByUnitHotelHostUserSecAndResStatusOrderByResCreatedAtDesc(
            Long hostSec, ReservationStatus status, Pageable pageable);

    Page<Reservation> findAllByOrderByResCreatedAtDesc(Pageable pageable);

    Page<Reservation> findByResStatusOrderByResCreatedAtDesc(ReservationStatus status, Pageable pageable);

    long countByUnitUniSecAndResStatusIn(Integer uniSec, List<ReservationStatus> statuses);

    /** HU #28: el usuario termino una estadia en el hotel (check-out ya paso) con alguno de esos estados. */
    boolean existsByUserUserSecAndUnitHotelHotSecAndResStatusInAndResCheckOutLessThanEqual(
            Long userSec, Integer hotSec, List<ReservationStatus> statuses, LocalDate checkOut);

    /**
     * Fase 2B (regla 6): true si la unidad tiene una reserva activa (PENDING / CONFIRMED)
     * que se cruza con [checkIn, checkOut). El dia de salida queda libre.
     */
    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Reservation r
            WHERE r.unit.uniSec = :unitId
              AND r.resStatus IN :occupying
              AND r.resCheckIn < :checkOut
              AND r.resCheckOut > :checkIn
            """)
    boolean existsOverlap(@Param("unitId") Integer unitId,
                          @Param("checkIn") LocalDate checkIn,
                          @Param("checkOut") LocalDate checkOut,
                          @Param("occupying") List<ReservationStatus> occupying);

    /**
     * Unidades del hotel con reservas activas (PENDING / CONFIRMED) que se cruzan
     * con el rango pedido. Se usa para la disponibilidad.
     * Cruce: existente.checkIn < nueva.checkOut && existente.checkOut > nueva.checkIn
     * (el dia de salida queda libre para otra reserva).
     */
    @Query("""
            SELECT DISTINCT r.unit.uniSec
            FROM Reservation r
            WHERE r.resStatus IN :occupying
              AND r.unit.hotel.hotSec = :hotSec
              AND r.resCheckIn < :checkOut
              AND r.resCheckOut > :checkIn
            """)
    List<Integer> findBusyUnitIdsInHotel(@Param("hotSec") Integer hotSec,
                                         @Param("checkIn") LocalDate checkIn,
                                         @Param("checkOut") LocalDate checkOut,
                                         @Param("occupying") List<ReservationStatus> occupying);

    /**
     * Calendario publico: reservas activas (PENDING / CONFIRMED) de unidades activas
     * del hotel que se cruzan con [from, to). Devuelve solo lo necesario para pintar
     * fechas: [uniSec, resCheckIn, resCheckOut] (no expone datos de la reserva).
     */
    @Query("""
            SELECT r.unit.uniSec, r.resCheckIn, r.resCheckOut
            FROM Reservation r
            WHERE r.resStatus IN :occupying
              AND r.unit.hotel.hotSec = :hotSec
              AND r.unit.uniState = 'A'
              AND r.resCheckIn < :to
              AND r.resCheckOut > :from
            """)
    List<Object[]> findActiveRangesInHotel(@Param("hotSec") Integer hotSec,
                                           @Param("from") LocalDate from,
                                           @Param("to") LocalDate to,
                                           @Param("occupying") List<ReservationStatus> occupying);
}
