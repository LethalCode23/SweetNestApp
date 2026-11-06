package com.dh.demo;

import com.dh.demo.entity.Hotel;
import com.dh.demo.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GET /api/hotel/suggestions — autocompletado del buscador (ciudades + hoteles).
 * Todas las peticiones van SIN usuario: el endpoint es publico.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SearchSuggestionsTests {

    private static final String URL = "/api/hotel/suggestions";

    @Autowired private MockMvc mockMvc;
    @Autowired private HotelRepository hotelRepository;

    @Test
    void sinParametro_devuelveListaVacia() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void terminoMenorADosCaracteresTrasTrim_devuelveListaVacia() throws Exception {
        mockMvc.perform(get(URL).param("q", "  b  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void coincidenciaParcialSinDistinguirMayusculas_ciudadesPrimeroLuegoHoteles() throws Exception {
        mockMvc.perform(get(URL).param("q", "  BOGO "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("CITY"))
                .andExpect(jsonPath("$[0].name").value("Bogotá"))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[1].type").value("HOTEL"))
                .andExpect(jsonPath("$[1].name").value("Hostal Andino Bogotá"));
    }

    @Test
    void sinCoincidencias_devuelveListaVacia() throws Exception {
        mockMvc.perform(get(URL).param("q", "zzzz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void devuelveComoMaximoDiez() throws Exception {
        mockMvc.perform(get(URL).param("q", "ot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(10))));
    }

    @Test
    void hotelInactivo_noSeSugiere() throws Exception {
        Hotel hotel = hotelRepository.findAll().stream()
                .filter(h -> h.getHotName().equals("Hostal Andino Bogotá"))
                .findFirst().orElseThrow();
        hotel.setHotState('I');
        hotelRepository.saveAndFlush(hotel);

        mockMvc.perform(get(URL).param("q", "andino"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
