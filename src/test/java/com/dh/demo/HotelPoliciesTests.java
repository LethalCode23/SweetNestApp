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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HU #26 (CRUD en el panel): las politicas viajan en el HotelDto del update.
 * null = no cambia; lista = reemplaza en ese orden; titulo vacio = 400.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HotelPoliciesTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private HotelRepository hotelRepository;

    private RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user("admin@sweetnest.com")
                .authorities(new SimpleGrantedAuthority("Administrador"));
    }

    private Hotel anyHotel() {
        return hotelRepository.findAll().get(0);
    }

    /** policiesJson = null omite el campo (no cambia las politicas). */
    private String updateBody(Hotel hotel, String policiesJson) {
        String policiesPart = policiesJson == null ? "" : ",\"policies\":" + policiesJson;
        return """
                {"hotName":"%s","hotDescription":"%s","hotAddress":"%s",
                 "hotCost":%d,"hotState":"A","hotCitSec":%d,
                 "typeId":%d%s}
                """.formatted(hotel.getHotName(), hotel.getHotDescription(),
                hotel.getHotAddress(), hotel.getHotCost(),
                hotel.getCity().getCitSec(), hotel.getType().getTypSec(), policiesPart);
    }

    private void update(Hotel hotel, String policiesJson, int expectedStatus) throws Exception {
        mockMvc.perform(put("/api/hotel/update/" + hotel.getHotSec()).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(hotel, policiesJson)))
                .andExpect(status().is(expectedStatus));
    }

    @Test
    void updateReplacesPoliciesInOrder() throws Exception {
        Hotel hotel = anyHotel();

        update(hotel, """
                [{"polTitle":"  Mascotas  ","polDescription":"Solo con aviso previo."},
                 {"polTitle":"Ruido","polDescription":"Silencio despues de las 10 PM."}]
                """, 200);

        mockMvc.perform(get("/api/hotel/findDetailById/" + hotel.getHotSec()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.policies", hasSize(2)))
                .andExpect(jsonPath("$.data.policies[0].polTitle").value("Mascotas"))
                .andExpect(jsonPath("$.data.policies[1].polTitle").value("Ruido"));
    }

    @Test
    void emptyListDeletesAllPolicies() throws Exception {
        Hotel hotel = anyHotel();

        update(hotel, "[]", 200);

        mockMvc.perform(get("/api/hotel/findDetailById/" + hotel.getHotSec()))
                .andExpect(jsonPath("$.data.policies", hasSize(0)));
    }

    @Test
    void missingPoliciesKeepsTheCurrentOnes() throws Exception {
        Hotel hotel = anyHotel();
        int before = hotel.getPolicies().size();

        update(hotel, null, 200);

        mockMvc.perform(get("/api/hotel/findDetailById/" + hotel.getHotSec()))
                .andExpect(jsonPath("$.data.policies", hasSize(before)));
    }

    @Test
    void blankTitleIsRejected() throws Exception {
        update(anyHotel(), """
                [{"polTitle":"   ","polDescription":"Algo"}]
                """, 400);
    }
}
