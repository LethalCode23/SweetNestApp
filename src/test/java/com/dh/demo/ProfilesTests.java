package com.dh.demo;

import com.jayway.jsonpath.JsonPath;
import com.dh.demo.entity.Profile;
import com.dh.demo.repository.IProfileRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Perfiles (rediseño del panel, Fase 5): alta y edición guardan los 4 campos y
 * siempre queda exactamente un perfil por defecto (lo usa el registro).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfilesTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private IProfileRepository profileRepository;

    private RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user("admin@sweetnest.com")
                .authorities(new SimpleGrantedAuthority("Administrador"));
    }

    private String json(String name, String state, String isDefault, String control) {
        return "{\"proName\":\"%s\",\"proState\":\"%s\",\"isDefault\":\"%s\",\"hasControlAccess\":\"%s\"}"
                .formatted(name, state, isDefault, control);
    }

    private ResultActions create(String body) throws Exception {
        return mockMvc.perform(post("/api/profile/save").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private ResultActions update(long id, String body) throws Exception {
        return mockMvc.perform(put("/api/profile/" + id).with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private long defaultsCount() {
        return profileRepository.findAll().stream()
                .filter(p -> p.getIsDefault() != null && p.getIsDefault() == 'S')
                .count();
    }

    private Profile byName(String name) {
        return profileRepository.findAll().stream()
                .filter(p -> p.getName().equals(name)).findFirst().orElseThrow();
    }

    @Test
    void crearGuardaLosCuatroCampos() throws Exception {
        String body = create(json("Recepcion", "A", "N", "S"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proName").value("Recepcion"))
                .andExpect(jsonPath("$.proState").value("A"))
                .andExpect(jsonPath("$.isDefault").value("N"))
                .andExpect(jsonPath("$.hasControlAccess").value("S"))
                .andReturn().getResponse().getContentAsString();

        Number id = JsonPath.read(body, "$.proId");
        assertTrue(id.longValue() > 0, "El id lo genera la BD");
    }

    @Test
    void editarGuardaPorDefectoYControlDeAcceso() throws Exception {
        String body = create(json("Soporte", "A", "N", "N")).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(body, "$.proId")).longValue();

        update(id, json("Soporte TI", "I", "N", "S"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proName").value("Soporte TI"))
                .andExpect(jsonPath("$.proState").value("I"))
                .andExpect(jsonPath("$.hasControlAccess").value("S"));
    }

    @Test
    void marcarOtroPorDefectoDesmarcaElAnterior() throws Exception {
        String body = create(json("Invitado", "A", "S", "N")).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(body, "$.proId")).longValue();

        assertEquals(1, defaultsCount());
        assertEquals('S', profileRepository.findById(id).orElseThrow().getIsDefault());
        assertEquals('N', byName("Usuario").getIsDefault());
    }

    @Test
    void noSePuedeDesmarcarElUnicoPerfilPorDefecto() throws Exception {
        Profile usuario = byName("Usuario");

        update(usuario.getId(), json("Usuario", "A", "N", "N"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1001));

        assertEquals(1, defaultsCount());
    }

    @Test
    void nombreVacioDevuelve400() throws Exception {
        create(json("  ", "A", "N", "N"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1001));
    }
}
