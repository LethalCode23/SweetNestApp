package com.dh.demo;

import com.dh.demo.entity.Hotel;
import com.dh.demo.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU #28: puntuar producto. Datos del DataInitializer:
 * viajera@ tiene estadia terminada SIN resena en "Hotel Valle Real Cali";
 * usuario@ solo tiene reservas futuras (no puede valorar);
 * "Hotel Poblado Suite" arranca con 2 resenas (5 y 4 -> 4.5).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewsAcceptanceTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private HotelRepository hotelRepository;

    private RequestPostProcessor user(String email) {
        return SecurityMockMvcRequestPostProcessors.user(email)
                .authorities(new SimpleGrantedAuthority("Usuario"));
    }

    private Hotel hotelNamed(String name) {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getHotName().equals(name))
                .findFirst().orElseThrow();
    }

    private String body(Integer stars, String comment) {
        String c = comment == null ? "null" : "\"" + comment + "\"";
        return "{\"stars\":" + stars + ",\"comment\":" + c + "}";
    }

    @Test
    void publicListShowsSeededSummary() throws Exception {
        Hotel poblado = hotelNamed("Hotel Poblado Suite");

        mockMvc.perform(get("/api/reviews/hotel/" + poblado.getHotSec()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ratingAvg").value(4.5))
                .andExpect(jsonPath("$.data.ratingCount").value(2))
                .andExpect(jsonPath("$.data.distribution[0]").value(1))   // cinco estrellas
                .andExpect(jsonPath("$.data.distribution[1]").value(1))   // cuatro estrellas
                .andExpect(jsonPath("$.data.reviews.content[0].userName").exists());
    }

    @Test
    void eligibleUserCreatesThenUpdatesOneReview() throws Exception {
        Hotel valle = hotelNamed("Hotel Valle Real Cali");
        String url = "/api/reviews/hotel/" + valle.getHotSec();

        mockMvc.perform(get(url + "/mine").with(user("viajera@sweetnest.com")))
                .andExpect(jsonPath("$.data.canReview").value(true))
                .andExpect(jsonPath("$.data.review").doesNotExist());

        mockMvc.perform(put(url).with(user("viajera@sweetnest.com"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(4, "  Muy bien  ")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ratingAvg").value(4.0))
                .andExpect(jsonPath("$.data.ratingCount").value(1))
                .andExpect(jsonPath("$.data.reviews.content[0].comment").value("Muy bien"))
                .andExpect(jsonPath("$.data.reviews.content[0].userName").value("Valentina R."));

        // Segunda vez: actualiza, no duplica
        mockMvc.perform(put(url).with(user("viajera@sweetnest.com"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(2, "")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ratingAvg").value(2.0))
                .andExpect(jsonPath("$.data.ratingCount").value(1))
                .andExpect(jsonPath("$.data.reviews.content[0].comment").doesNotExist());

        // El detalle del hotel ya trae el promedio nuevo
        mockMvc.perform(get("/api/hotel/findDetailById/" + valle.getHotSec()))
                .andExpect(jsonPath("$.data.ratingAvg").value(2.0))
                .andExpect(jsonPath("$.data.ratingCount").value(1));
    }

    @Test
    void userWithoutFinishedStayIsRejected() throws Exception {
        Hotel poblado = hotelNamed("Hotel Poblado Suite"); // usuario@ tiene una reserva FUTURA aqui

        mockMvc.perform(get("/api/reviews/hotel/" + poblado.getHotSec() + "/mine").with(user("usuario@sweetnest.com")))
                .andExpect(jsonPath("$.data.canReview").value(false));

        mockMvc.perform(put("/api/reviews/hotel/" + poblado.getHotSec()).with(user("usuario@sweetnest.com"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(5, "Genial")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1021));
    }

    @Test
    void starsOutOfRangeAreRejected() throws Exception {
        Hotel valle = hotelNamed("Hotel Valle Real Cali");
        for (int stars : new int[]{0, 6}) {
            mockMvc.perform(put("/api/reviews/hotel/" + valle.getHotSec()).with(user("viajera@sweetnest.com"))
                            .contentType(MediaType.APPLICATION_JSON).content(body(stars, null)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void anonymousCannotReview() throws Exception {
        Hotel valle = hotelNamed("Hotel Valle Real Cali");
        mockMvc.perform(put("/api/reviews/hotel/" + valle.getHotSec())
                        .contentType(MediaType.APPLICATION_JSON).content(body(5, null)))
                .andExpect(status().is(anyOf(is(401), is(403))));
    }
}
