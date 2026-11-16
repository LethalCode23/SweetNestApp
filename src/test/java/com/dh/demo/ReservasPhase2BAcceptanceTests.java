package com.dh.demo;

import com.jayway.jsonpath.JsonPath;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelUnit;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.HotelUnitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Fase 2 · Bloque B: cruce de disponibilidad al crear una reserva (regla 6 → 409 / 1011).
 * Cruce: existente.checkIn < nueva.checkOut && existente.checkOut > nueva.checkIn.
 * Fechas en 2032 para no chocar con otros tests (2030 / 2031) ni con el seed (hoy ± 40 dias).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservasPhase2BAcceptanceTests {

    private static final String MULTI_UNIT_HOTEL = "Hotel Valle Real Cali"; // 2 unidades
    private static final int UNIT_UNAVAILABLE = 1011;

    @Autowired private MockMvc mockMvc;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private HotelUnitRepository hotelUnitRepository;

    // ── helpers ─────────────────────────────────────────────────────────────

    private RequestPostProcessor guest() {
        return SecurityMockMvcRequestPostProcessors.user("usuario@sweetnest.com")
                .authorities(new SimpleGrantedAuthority("Usuario"));
    }

    private List<HotelUnit> units() {
        Hotel hotel = hotelRepository.findAll().stream()
                .filter(h -> h.getHotName().equals(MULTI_UNIT_HOTEL))
                .findFirst().orElseThrow();
        return hotelUnitRepository.findByHotelHotSecAndUniStateOrderByUniNameAsc(hotel.getHotSec(), 'A');
    }

    private int unitA() {
        return units().get(0).getUniSec();
    }

    private ResultActions reserve(int unitId, String in, String out) throws Exception {
        String json = "{\"unitId\":%d,\"checkIn\":\"%s\",\"checkOut\":\"%s\",\"guests\":1}"
                .formatted(unitId, in, out);
        return mockMvc.perform(post("/api/reservations").with(guest())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private int reserveOk(int unitId, String in, String out) throws Exception {
        String body = reserve(unitId, in, out)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.data.resSec");
    }

    private void expectUnavailable(ResultActions result) throws Exception {
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(UNIT_UNAVAILABLE));
    }

    // ── casos ───────────────────────────────────────────────────────────────

    @Test
    void mismoRangoSobreReservaActivaDevuelve409() throws Exception {
        reserveOk(unitA(), "2032-01-10", "2032-01-14");
        expectUnavailable(reserve(unitA(), "2032-01-10", "2032-01-14"));
    }

    @Test
    void cruceParcialDevuelve409() throws Exception {
        reserveOk(unitA(), "2032-02-10", "2032-02-14");
        expectUnavailable(reserve(unitA(), "2032-02-12", "2032-02-16"));
        expectUnavailable(reserve(unitA(), "2032-02-08", "2032-02-11"));
    }

    @Test
    void rangoQueEnvuelveLaReservaDevuelve409() throws Exception {
        reserveOk(unitA(), "2032-03-10", "2032-03-12");
        expectUnavailable(reserve(unitA(), "2032-03-05", "2032-03-20"));
    }

    @Test
    void elDiaDeSalidaQuedaLibre() throws Exception {
        reserveOk(unitA(), "2032-04-10", "2032-04-14");
        reserve(unitA(), "2032-04-14", "2032-04-16").andExpect(status().isCreated());
        reserve(unitA(), "2032-04-08", "2032-04-10").andExpect(status().isCreated());
    }

    @Test
    void reservaCanceladaNoBloquea() throws Exception {
        int id = reserveOk(unitA(), "2032-05-10", "2032-05-14");

        mockMvc.perform(patch("/api/reservations/" + id + "/cancel").with(guest()))
                .andExpect(status().isOk());

        reserve(unitA(), "2032-05-10", "2032-05-14").andExpect(status().isCreated());
    }

    @Test
    void otraUnidadDelMismoHotelSePuedeReservar() throws Exception {
        List<HotelUnit> us = units();
        reserveOk(us.get(0).getUniSec(), "2032-06-10", "2032-06-14");
        reserve(us.get(1).getUniSec(), "2032-06-10", "2032-06-14").andExpect(status().isCreated());
    }
}
