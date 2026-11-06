package com.dh.demo;

import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelUnit;
import com.dh.demo.entity.Reservation;
import com.dh.demo.entity.User;
import com.dh.demo.entity.enums.ReservationStatus;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.HotelUnitRepository;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GET /api/hotel/{hotSec}/unavailable-dates (publico): dias sin ninguna unidad activa libre.
 * Se usan fechas de 2031 para no cruzarse con las reservas seed (relativas a hoy).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HotelUnavailableDatesTests {

    private static final String SINGLE_UNIT_HOTEL = "Costa Caribe Barranquilla"; // casa, 1 unidad
    private static final String MULTI_UNIT_HOTEL = "Hotel Valle Real Cali";      // 2 unidades
    private static final String FROM = "2031-03-01";
    private static final String TO = "2031-04-01";

    @Autowired private MockMvc mockMvc;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private HotelUnitRepository hotelUnitRepository;
    @Autowired private IUserRepository userRepository;
    @Autowired private ReservationRepository reservationRepository;

    // ── helpers ─────────────────────────────────────────────────────────────

    private Hotel hotel(String name) {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getHotName().equals(name))
                .findFirst().orElseThrow();
    }

    private List<HotelUnit> units(Hotel hotel) {
        return hotelUnitRepository.findByHotelHotSecAndUniStateOrderByUniNameAsc(hotel.getHotSec(), 'A');
    }

    private void reserve(HotelUnit unit, String checkIn, String checkOut, ReservationStatus status) {
        User guest = userRepository.findAll().get(0);
        Reservation r = new Reservation();
        r.setUser(guest);
        r.setUnit(unit);
        r.setResCheckIn(LocalDate.parse(checkIn));
        r.setResCheckOut(LocalDate.parse(checkOut));
        r.setResGuests(1);
        r.setResPrice(unit.getUniPrice());
        r.setResTotal(unit.getUniPrice());
        r.setResStatus(status);
        reservationRepository.saveAndFlush(r);
    }

    private ResultActions call(Hotel hotel) throws Exception {
        return mockMvc.perform(get("/api/hotel/" + hotel.getHotSec() + "/unavailable-dates")
                .param("from", FROM).param("to", TO));
    }

    // ── casos ───────────────────────────────────────────────────────────────

    @Test
    void sinTokenResponde200ConRangoPorDefecto() throws Exception {
        Hotel h = hotel(SINGLE_UNIT_HOTEL);
        mockMvc.perform(get("/api/hotel/" + h.getHotSec() + "/unavailable-dates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.hotSec").value(h.getHotSec()))
                .andExpect(jsonPath("$.data.from").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.data.to").value(LocalDate.now().plusMonths(6).toString()))
                .andExpect(jsonPath("$.data.totalUnits").value(1));
    }

    @Test
    void unaUnidadConfirmadaBloqueaNochesYDejaLibreElDiaDeSalida() throws Exception {
        Hotel h = hotel(SINGLE_UNIT_HOTEL);
        reserve(units(h).get(0), "2031-03-10", "2031-03-12", ReservationStatus.CONFIRMED);

        call(h).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unavailableDates", contains("2031-03-10", "2031-03-11")));
    }

    @Test
    void pendingBloqueaYCanceladaOCompletadaNo() throws Exception {
        Hotel h = hotel(SINGLE_UNIT_HOTEL);
        HotelUnit u = units(h).get(0);
        reserve(u, "2031-03-05", "2031-03-06", ReservationStatus.PENDING);
        reserve(u, "2031-03-15", "2031-03-17", ReservationStatus.CANCELLED);
        reserve(u, "2031-03-20", "2031-03-22", ReservationStatus.COMPLETED);

        call(h).andExpect(jsonPath("$.data.unavailableDates", contains("2031-03-05")));
    }

    @Test
    void reservaQueEmpiezaAntesDelRangoSeRecorta() throws Exception {
        Hotel h = hotel(SINGLE_UNIT_HOTEL);
        reserve(units(h).get(0), "2031-02-27", "2031-03-02", ReservationStatus.CONFIRMED);

        call(h).andExpect(jsonPath("$.data.unavailableDates", contains("2031-03-01")));
    }

    @Test
    void multiUnidadSoloBloqueaCuandoTodasEstanOcupadas() throws Exception {
        Hotel h = hotel(MULTI_UNIT_HOTEL);
        List<HotelUnit> us = units(h);
        reserve(us.get(0), "2031-03-10", "2031-03-14", ReservationStatus.CONFIRMED);
        reserve(us.get(1), "2031-03-12", "2031-03-16", ReservationStatus.PENDING);

        call(h).andExpect(jsonPath("$.data.totalUnits").value(2))
                .andExpect(jsonPath("$.data.unavailableDates", contains("2031-03-12", "2031-03-13")));
    }

    @Test
    void reservasDeUnidadesInactivasNoCuentan() throws Exception {
        Hotel h = hotel(MULTI_UNIT_HOTEL);
        List<HotelUnit> us = units(h);
        HotelUnit inactive = us.get(1);
        reserve(inactive, "2031-03-10", "2031-03-12", ReservationStatus.CONFIRMED);
        inactive.setUniState('I');
        hotelUnitRepository.saveAndFlush(inactive);

        // queda 1 unidad activa y libre -> nada bloqueado
        call(h).andExpect(jsonPath("$.data.totalUnits").value(1))
                .andExpect(jsonPath("$.data.unavailableDates", empty()));

        // si se ocupa la unica activa, se bloquea
        reserve(us.get(0), "2031-03-10", "2031-03-11", ReservationStatus.CONFIRMED);
        call(h).andExpect(jsonPath("$.data.unavailableDates", contains("2031-03-10")));
    }

    @Test
    void hotelInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/hotel/999999/unavailable-dates"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rangoInvalidoDevuelve400() throws Exception {
        Hotel h = hotel(SINGLE_UNIT_HOTEL);
        mockMvc.perform(get("/api/hotel/" + h.getHotSec() + "/unavailable-dates")
                        .param("from", "2031-03-10").param("to", "2031-03-10"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/hotel/" + h.getHotSec() + "/unavailable-dates")
                        .param("from", "2031-01-01").param("to", "2032-06-01"))
                .andExpect(status().isBadRequest());
    }
}
