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
 * GET /api/hotel-unit/hotel/{hotSec}/available (publico): unidades libres en [checkIn, checkOut).
 * Fechas en 2034 para no chocar con otros tests ni con el seed.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HotelUnitAvailabilityTests {

    private static final String MULTI_UNIT_HOTEL = "Hotel Valle Real Cali"; // 2 unidades

    @Autowired private MockMvc mockMvc;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private HotelUnitRepository hotelUnitRepository;
    @Autowired private IUserRepository userRepository;
    @Autowired private ReservationRepository reservationRepository;

    private Hotel hotel() {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getHotName().equals(MULTI_UNIT_HOTEL))
                .findFirst().orElseThrow();
    }

    private List<HotelUnit> units(Hotel h) {
        return hotelUnitRepository.findByHotelHotSecAndUniStateOrderByUniNameAsc(h.getHotSec(), 'A');
    }

    private void reserve(HotelUnit unit, String in, String out, ReservationStatus status) {
        User guest = userRepository.findAll().get(0);
        Reservation r = new Reservation();
        r.setUser(guest);
        r.setUnit(unit);
        r.setResCheckIn(LocalDate.parse(in));
        r.setResCheckOut(LocalDate.parse(out));
        r.setResGuests(1);
        r.setResPrice(unit.getUniPrice());
        r.setResTotal(unit.getUniPrice());
        r.setResStatus(status);
        reservationRepository.saveAndFlush(r);
    }

    private ResultActions available(Hotel h, String in, String out) throws Exception {
        return mockMvc.perform(get("/api/hotel-unit/hotel/" + h.getHotSec() + "/available")
                .param("checkIn", in).param("checkOut", out));
    }

    @Test
    void sinTokenDevuelveTodasLasUnidadesLibres() throws Exception {
        Hotel h = hotel();
        available(h, "2034-01-10", "2034-01-12")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(units(h).size())));
    }

    @Test
    void unidadReservadaNoAparece() throws Exception {
        Hotel h = hotel();
        List<HotelUnit> us = units(h);
        reserve(us.get(0), "2034-02-10", "2034-02-14", ReservationStatus.CONFIRMED);

        available(h, "2034-02-12", "2034-02-16")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(us.size() - 1)))
                .andExpect(jsonPath("$.data[*].uniSec", not(hasItem(us.get(0).getUniSec()))));
    }

    @Test
    void reservaCanceladaNoQuitaLaUnidad() throws Exception {
        Hotel h = hotel();
        List<HotelUnit> us = units(h);
        reserve(us.get(0), "2034-03-10", "2034-03-14", ReservationStatus.CANCELLED);

        available(h, "2034-03-10", "2034-03-14")
                .andExpect(jsonPath("$.data", hasSize(us.size())));
    }

    @Test
    void elDiaDeSalidaQuedaLibre() throws Exception {
        Hotel h = hotel();
        List<HotelUnit> us = units(h);
        reserve(us.get(0), "2034-04-10", "2034-04-14", ReservationStatus.PENDING);

        available(h, "2034-04-14", "2034-04-16")
                .andExpect(jsonPath("$.data[*].uniSec", hasItem(us.get(0).getUniSec())));
    }

    @Test
    void rangoInvalidoOSinFechasDevuelve400() throws Exception {
        Hotel h = hotel();
        available(h, "2034-05-10", "2034-05-10")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1014));

        mockMvc.perform(get("/api/hotel-unit/hotel/" + h.getHotSec() + "/available"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1014));
    }
}
