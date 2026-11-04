package com.dh.demo;

import com.jayway.jsonpath.JsonPath;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.Reservation;
import com.dh.demo.entity.User;
import com.dh.demo.entity.enums.ReservationStatus;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.repository.ReservationRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Criterios de aceptacion de la Fase 2 - Bloque A (anfitrion del hotel):
 * marca userHost en usuarios, asignacion hotUserSec, bandeja /incoming
 * y confirmacion por el anfitrion.
 *
 * Autenticacion con post-processors (.with(...)), igual que en Fase 1.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservasPhase2AcceptanceTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private IUserRepository userRepository;
    @Autowired private ReservationRepository reservationRepository;

    // ── helpers ─────────────────────────────────────────────────────────────

    private RequestPostProcessor user(String email) {
        return SecurityMockMvcRequestPostProcessors.user(email)
                .authorities(new SimpleGrantedAuthority("Usuario"));
    }

    private RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user("admin@sweetnest.com")
                .authorities(new SimpleGrantedAuthority("Administrador"));
    }

    private User findUser(String email) {
        return userRepository.findByUserEmail(email).orElseThrow();
    }

    private User hostA() {
        return findUser("anfitrion@sweetnest.com");
    }

    private User hostB() {
        return findUser("anfitrion2@sweetnest.com");
    }

    /** Reserva PENDING sembrada en un hotel del anfitrion A. */
    private Reservation pendingOfHostA() {
        User a = hostA();
        return reservationRepository.findAll().stream()
                .filter(r -> r.getResStatus() == ReservationStatus.PENDING
                        && r.getUnit().getHotel().getHost() != null
                        && r.getUnit().getHotel().getHost().getUserSec().equals(a.getUserSec()))
                .findFirst().orElseThrow();
    }

    /** Reservas cuyo hotel tiene como anfitrion al usuario dado. */
    private long countIncomingOf(User host) {
        return reservationRepository.findAll().stream()
                .filter(r -> {
                    Hotel h = r.getUnit().getHotel();
                    return h.getHost() != null && h.getHost().getUserSec().equals(host.getUserSec());
                })
                .count();
    }

    private String userUpdateBody(User u, String host) {
        String hostPart = host == null ? "" : ",\"host\":\"" + host + "\"";
        return "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\",\"profileId\":%d%s}"
                .formatted(u.getUserFirstName(), u.getUserLastName(), u.getUserEmail(),
                        u.getProfile().getId(), hostPart);
    }

    private String hotelUpdateBody(Hotel hotel, Long hostId) {
        String hostPart = hostId == null ? "" : ",\"hostId\":" + hostId;
        return """
                {"hotName":"%s","hotDescription":"%s","hotAddress":"%s",
                 "hotCost":%d,"hotState":"A","hotCitSec":%d,
                 "typeId":%d%s}
                """.formatted(hotel.getHotName(), hotel.getHotDescription(),
                hotel.getHotAddress(), hotel.getHotCost(),
                hotel.getCity().getCitSec(), hotel.getType().getTypSec(), hostPart);
    }

    // ── 1. Marca de anfitrion en usuarios (userHost) ────────────────────────

    @Test
    void adminCanMarkUserAsHost() throws Exception {
        User guest = findUser("usuario@sweetnest.com");

        mockMvc.perform(put("/api/users/" + guest.getUserSec()).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateBody(guest, "S")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.host").value("S"));
    }

    @Test
    void nonAdminCannotChangeHostFlag() throws Exception {
        User guest = findUser("usuario@sweetnest.com");

        mockMvc.perform(put("/api/users/" + guest.getUserSec()).with(user("usuario@sweetnest.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateBody(guest, "S")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1018));
    }

    @Test
    void hostFlagRejectsInvalidValue() throws Exception {
        User guest = findUser("usuario@sweetnest.com");

        mockMvc.perform(put("/api/users/" + guest.getUserSec()).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateBody(guest, "X")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1001));
    }

    // ── 2. Asignar el anfitrion al hotel (hotUserSec) ───────────────────────

    @Test
    void markedHostUserCanBeAssignedToHotel() throws Exception {
        User guest = findUser("usuario@sweetnest.com");
        Hotel hotel = hotelRepository.findAll().get(0);

        // 1) el admin marca al usuario como anfitrion
        mockMvc.perform(put("/api/users/" + guest.getUserSec()).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateBody(guest, "S")))
                .andExpect(status().isOk());

        // 2) y ahora si puede ser host del hotel
        mockMvc.perform(put("/api/hotel/update/" + hotel.getHotSec()).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hotelUpdateBody(hotel, guest.getUserSec())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hostId").value(guest.getUserSec().intValue()));

        // 3) el detalle expone el anfitrion
        mockMvc.perform(get("/api/hotel/findDetailById/" + hotel.getHotSec()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hostId").value(guest.getUserSec().intValue()));
    }

    @Test
    void assigningNonHostUserAsHotelHostIsRejected() throws Exception {
        User notHost = findUser("usuario@sweetnest.com"); // userHost = 'N'
        Hotel hotel = hotelRepository.findAll().get(0);

        mockMvc.perform(put("/api/hotel/update/" + hotel.getHotSec()).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hotelUpdateBody(hotel, notHost.getUserSec())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(1020));
    }

    @Test
    void assigningUnknownHostIsRejected() throws Exception {
        Hotel hotel = hotelRepository.findAll().get(0);

        mockMvc.perform(put("/api/hotel/update/" + hotel.getHotSec()).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hotelUpdateBody(hotel, 999999L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(1004));
    }

    // ── 3. Bandeja /incoming ────────────────────────────────────────────────

    @Test
    void incomingRequiresToken() throws Exception {
        mockMvc.perform(get("/api/reservations/incoming"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void incomingOnlyShowsReservationsOfOwnHotels() throws Exception {
        User a = hostA();
        long expected = countIncomingOf(a);
        assertTrue(expected > 0, "hostA debe tener reservas sembradas en sus hoteles");

        String body = mockMvc.perform(get("/api/reservations/incoming")
                        .with(user("anfitrion@sweetnest.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize((int) expected)))
                .andReturn().getResponse().getContentAsString();

        List<Integer> hotSecs = JsonPath.read(body, "$.data.content[*].hotSec");
        for (Integer sec : hotSecs) {
            Hotel h = hotelRepository.findById(sec).orElseThrow();
            assertTrue(h.getHost() != null && h.getHost().getUserSec().equals(a.getUserSec()),
                    "el hotel " + sec + " no pertenece al anfitrion A");
        }
    }

    @Test
    void incomingIsEmptyForNonHostUser() throws Exception {
        mockMvc.perform(get("/api/reservations/incoming")
                        .with(user("usuario@sweetnest.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", empty()));
    }

    @Test
    void incomingFiltersByStatus() throws Exception {
        User a = hostA();
        long expected = reservationRepository.findAll().stream()
                .filter(r -> r.getResStatus() == ReservationStatus.PENDING
                        && r.getUnit().getHotel().getHost() != null
                        && r.getUnit().getHotel().getHost().getUserSec().equals(a.getUserSec()))
                .count();
        assertTrue(expected > 0, "hostA debe tener reservas PENDING sembradas");

        mockMvc.perform(get("/api/reservations/incoming?status=PENDING")
                        .with(user("anfitrion@sweetnest.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize((int) expected)))
                .andExpect(jsonPath("$.data.content[*].status", everyItem(is("PENDING"))));
    }

    // ── 4. Confirmar: admin o anfitrion del hotel ───────────────────────────

    @Test
    void hostCanConfirmReservationOfOwnHotel() throws Exception {
        Reservation pending = pendingOfHostA();

        mockMvc.perform(patch("/api/reservations/" + pending.getResSec() + "/confirm")
                        .with(user("anfitrion@sweetnest.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    void hostCannotConfirmReservationOfAnotherHotel() throws Exception {
        Reservation pending = pendingOfHostA();

        mockMvc.perform(patch("/api/reservations/" + pending.getResSec() + "/confirm")
                        .with(user("anfitrion2@sweetnest.com")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1018));
    }

    @Test
    void confirmFailsWhenHotelHasNoHost() throws Exception {
        Reservation pending = pendingOfHostA();
        Hotel hotel = pending.getUnit().getHotel();
        hotel.setHost(null);
        hotelRepository.save(hotel);

        mockMvc.perform(patch("/api/reservations/" + pending.getResSec() + "/confirm")
                        .with(user("anfitrion@sweetnest.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(1019));
    }

    @Test
    void guestCannotConfirmReservationOfHostHotel() throws Exception {
        Reservation pending = pendingOfHostA();

        mockMvc.perform(patch("/api/reservations/" + pending.getResSec() + "/confirm")
                        .with(user("usuario@sweetnest.com")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1018));
    }
}
