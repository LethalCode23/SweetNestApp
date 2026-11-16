package com.dh.demo;

import com.dh.demo.dto.request.CreateReservationRequest;
import com.dh.demo.dto.response.ReservationResponse;
import com.dh.demo.entity.HotelUnit;
import com.dh.demo.entity.enums.ReservationStatus;
import com.dh.demo.exception.UnitUnavailableException;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.HotelUnitRepository;
import com.dh.demo.repository.ReservationRepository;
import com.dh.demo.service.IReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fase 2 · Bloque B: dos reservas simultaneas de la misma unidad y el mismo rango → solo una se guarda.
 * Sin @Transactional: cada hilo necesita su propia transaccion para que el bloqueo (FOR UPDATE) actue.
 * Por eso lo creado se borra a mano en @AfterEach.
 */
@SpringBootTest
class ReservasConcurrencyTests {

    private static final String SINGLE_UNIT_HOTEL = "Costa Caribe Barranquilla"; // casa, 1 unidad
    private static final LocalDate CHECK_IN = LocalDate.of(2033, 1, 10);
    private static final LocalDate CHECK_OUT = LocalDate.of(2033, 1, 14);

    @Autowired private IReservationService reservationService;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private HotelUnitRepository hotelUnitRepository;
    @Autowired private ReservationRepository reservationRepository;

    private final List<Integer> created = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        reservationRepository.deleteAllById(created);
    }

    private Integer unitId() {
        Integer hotSec = hotelRepository.findAll().stream()
                .filter(h -> h.getHotName().equals(SINGLE_UNIT_HOTEL))
                .findFirst().orElseThrow().getHotSec();
        List<HotelUnit> units = hotelUnitRepository.findByHotelHotSecAndUniStateOrderByUniNameAsc(hotSec, 'A');
        return units.get(0).getUniSec();
    }

    @Test
    void dosReservasSimultaneasSoloUnaSeGuarda() throws Exception {
        Integer unitId = unitId();
        CountDownLatch start = new CountDownLatch(1);

        Callable<ReservationResponse> attempt = () -> {
            CreateReservationRequest req = new CreateReservationRequest();
            req.setUnitId(unitId);
            req.setCheckIn(CHECK_IN);
            req.setCheckOut(CHECK_OUT);
            req.setGuests(1);
            start.await();
            return reservationService.create(req, "usuario@sweetnest.com");
        };

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<ReservationResponse> f1 = pool.submit(attempt);
            Future<ReservationResponse> f2 = pool.submit(attempt);
            start.countDown();

            int ok = 0;
            int rejected = 0;
            for (Future<ReservationResponse> f : List.of(f1, f2)) {
                try {
                    created.add(f.get(20, TimeUnit.SECONDS).getResSec());
                    ok++;
                } catch (ExecutionException e) {
                    assertInstanceOf(UnitUnavailableException.class, e.getCause(),
                            "La reserva rechazada debe fallar por cruce (1011), no por otra causa");
                    rejected++;
                }
            }

            assertEquals(1, ok, "Solo una reserva debe guardarse");
            assertEquals(1, rejected, "La otra debe rechazarse con UNIT_UNAVAILABLE");
            assertTrue(reservationRepository.existsOverlap(unitId, CHECK_IN, CHECK_OUT,
                    List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)));
        } finally {
            pool.shutdownNow();
        }
    }
}
