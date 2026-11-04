package com.dh.demo;

import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.User;
import com.dh.demo.repository.FavoriteRepository;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Favoritos: usuario + hotel, unico, requiere sesion.
 * El seed le da 2 favoritos a usuario@sweetnest.com; anfitrion@sweetnest.com no tiene.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FavoritesAcceptanceTests {

    private static final String GUEST = "usuario@sweetnest.com";
    private static final String OTHER = "anfitrion@sweetnest.com";

    @Autowired private MockMvc mockMvc;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private IUserRepository userRepository;
    @Autowired private FavoriteRepository favoriteRepository;

    // ── helpers ─────────────────────────────────────────────────────────────

    private RequestPostProcessor user(String email) {
        return SecurityMockMvcRequestPostProcessors.user(email)
                .authorities(new SimpleGrantedAuthority("Usuario"));
    }

    private User findUser(String email) {
        return userRepository.findByUserEmail(email).orElseThrow();
    }

    /** Un hotel activo que OTHER todavia no tiene en favoritos. */
    private Hotel activeHotel() {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getHotState() != null && h.getHotState() == 'A')
                .findFirst().orElseThrow();
    }

    private long countOf(String email) {
        return favoriteRepository.countByUserUserSec(findUser(email).getUserSec());
    }

    // ── sesion ──────────────────────────────────────────────────────────────

    @Test
    void favoritesWithoutTokenAreRejectedWith401() throws Exception {
        mockMvc.perform(get("/api/favorites")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/favorites/ids")).andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/favorites/1")).andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/favorites/1")).andExpect(status().isUnauthorized());
    }

    // ── guardar / quitar ────────────────────────────────────────────────────

    @Test
    void addingTheSameHotelTwiceKeepsASingleFavorite() throws Exception {
        Integer hotSec = activeHotel().getHotSec();

        mockMvc.perform(put("/api/favorites/" + hotSec).with(user(OTHER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hotel guardado en favoritos"))
                .andExpect(jsonPath("$.data.favSec").isNumber())
                .andExpect(jsonPath("$.data.hotel.hotSec").value(hotSec));

        mockMvc.perform(put("/api/favorites/" + hotSec).with(user(OTHER)))
                .andExpect(status().isOk());

        assertEquals(1, countOf(OTHER));
    }

    @Test
    void removingAFavoriteDeletesItAndIsIdempotent() throws Exception {
        Integer hotSec = activeHotel().getHotSec();
        mockMvc.perform(put("/api/favorites/" + hotSec).with(user(OTHER))).andExpect(status().isOk());

        mockMvc.perform(delete("/api/favorites/" + hotSec).with(user(OTHER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hotel quitado de favoritos"));
        assertEquals(0, countOf(OTHER));

        // Quitar algo que ya no esta tambien responde 200.
        mockMvc.perform(delete("/api/favorites/" + hotSec).with(user(OTHER)))
                .andExpect(status().isOk());
    }

    @Test
    void addingANonExistentHotelReturns404With1007() throws Exception {
        mockMvc.perform(put("/api/favorites/999999").with(user(OTHER)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(1007));
    }

    @Test
    void addingAnInactiveHotelReturns409With1016() throws Exception {
        Hotel hotel = activeHotel();
        hotel.setHotState('I');
        hotelRepository.saveAndFlush(hotel);

        mockMvc.perform(put("/api/favorites/" + hotel.getHotSec()).with(user(OTHER)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(1016));
    }

    // ── listas ──────────────────────────────────────────────────────────────

    @Test
    void listOnlyReturnsTheFavoritesOfTheTokenUser() throws Exception {
        mockMvc.perform(get("/api/favorites").with(user(GUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content[0].hotel.hotName").isString());

        mockMvc.perform(get("/api/favorites").with(user(OTHER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    void idsReturnsTheHotelIdsOfTheTokenUser() throws Exception {
        List<Integer> expected = favoriteRepository.findHotelIdsByUser(findUser(GUEST).getUserSec());

        mockMvc.perform(get("/api/favorites/ids").with(user(GUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data", containsInAnyOrder(expected.toArray())));
    }
}
