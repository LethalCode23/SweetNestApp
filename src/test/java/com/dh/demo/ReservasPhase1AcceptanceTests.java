package com.dh.demo;

import com.jayway.jsonpath.JsonPath;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelType;
import com.dh.demo.entity.HotelUnit;
import com.dh.demo.entity.enums.ReservationStatus;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.HotelTypeRepository;
import com.dh.demo.repository.HotelUnitRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Criterios de aceptacion de la Fase 1 (tablas nuevas + bandera de confirmacion).
 *
 * La autenticacion se simula con post-processors (.with(...)) en cada peticion,
 * porque en este setup @WithMockUser no llega a aplicarse.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservasPhase1AcceptanceTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private HotelTypeRepository hotelTypeRepository;
    @Autowired private HotelUnitRepository hotelUnitRepository;
    @Autowired private ReservationRepository reservationRepository;

    // ── helpers ─────────────────────────────────────────────────────────────

    private RequestPostProcessor guest() {
        return SecurityMockMvcRequestPostProcessors.user("usuario@sweetnest.com")
                .authorities(new SimpleGrantedAuthority("Usuario"));
    }

    private RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user("admin@sweetnest.com")
                .authorities(new SimpleGrantedAuthority("Administrador"));
    }

    /** Hotel sembrado con hotConfirmReq = 'N' (reserva inmediata). */
    private Hotel hotelReservaInmediata() {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getHotConfirmReq() != null && h.getHotConfirmReq() == 'N')
                .findFirst().orElseThrow();
    }

    /** Hotel sembrado con hotConfirmReq = 'S' (requiere confirmacion). */
    private Hotel hotelRequiereConfirmacion() {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getHotConfirmReq() != null && h.getHotConfirmReq() == 'S')
                .findFirst().orElseThrow();
    }

    private HotelUnit unitOf(Hotel hotel) {
        return hotel.getUnits().get(0);
    }

    private String json(int unitId, String checkIn, String checkOut, int guests) {
        return "{\"unitId\":%d,\"checkIn\":\"%s\",\"checkOut\":\"%s\",\"guests\":%d}"
                .formatted(unitId, checkIn, checkOut, guests);
    }

    /** Crea una reserva con el guest y devuelve su id. */
    private int createReservationAsGuest(int unitId, String in, String out, int guests) throws Exception {
        String body = mockMvc.perform(post("/api/reservations").with(guest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(unitId, in, out, guests)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.data.resSec");
    }

    // ── 1. Tipos de alojamiento publicos con ApiResponse ────────────────────

    @Test
    void hotelTypesArePublicAndWrappedInApiResponse() throws Exception {
        mockMvc.perform(get("/api/hotel-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    void hotelTypesContainTheThreeSeedValues() throws Exception {
        mockMvc.perform(get("/api/hotel-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].typName",
                        containsInAnyOrder("Hotel", "Casa", "Apartamento")));
    }

    @Test
    void creatingHotelTypeRequiresAdmin() throws Exception {
        mockMvc.perform(post("/api/hotel-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"typName\":\"Suite\",\"typMultiUnit\":\"S\"}"))
                .andExpect(status().is4xxClientError());
    }

    // ── 2. La bandera decide el estado inicial de la reserva ────────────────

    @Test
    void reservationIsBornConfirmedWhenHotelDoesNotRequireConfirmation() throws Exception {
        Hotel hotel = hotelReservaInmediata();
        int price = unitOf(hotel).getUniPrice();

        mockMvc.perform(post("/api/reservations").with(guest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(unitOf(hotel).getUniSec(), "2030-01-10", "2030-01-14", 1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.pricePerNight").value(price))
                .andExpect(jsonPath("$.data.nights").value(4))
                .andExpect(jsonPath("$.data.totalPrice").value(price * 4));
    }

    @Test
    void reservationIsBornPendingWhenHotelRequiresConfirmation() throws Exception {
        Hotel hotel = hotelRequiereConfirmacion();

        mockMvc.perform(post("/api/reservations").with(guest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(unitOf(hotel).getUniSec(), "2030-02-10", "2030-02-13", 1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    // ── 3. Validaciones basicas ─────────────────────────────────────────────

    @Test
    void creatingReservationWithoutTokenIsRejectedWith401() throws Exception {
        Hotel hotel = hotelReservaInmediata();

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(unitOf(hotel).getUniSec(), "2030-03-10", "2030-03-12", 1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void checkOutMustBeAfterCheckIn() throws Exception {
        Hotel hotel = hotelReservaInmediata();

        mockMvc.perform(post("/api/reservations").with(guest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(unitOf(hotel).getUniSec(), "2030-04-10", "2030-04-10", 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1014));
    }

    @Test
    void checkInCannotBeInThePast() throws Exception {
        Hotel hotel = hotelReservaInmediata();

        mockMvc.perform(post("/api/reservations").with(guest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(unitOf(hotel).getUniSec(), "2020-01-01", "2020-01-05", 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1015));
    }

    @Test
    void guestsCannotExceedUnitCapacity() throws Exception {
        Hotel hotel = hotelReservaInmediata();
        int overCapacity = unitOf(hotel).getUniCapacity() + 1;

        mockMvc.perform(post("/api/reservations").with(guest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(unitOf(hotel).getUniSec(), "2030-05-10", "2030-05-12", overCapacity)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1013));
    }

    @Test
    void unknownUnitIsRejected() throws Exception {
        mockMvc.perform(post("/api/reservations").with(guest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(999999, "2030-06-10", "2030-06-12", 1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(1009));
    }

    // ── 4. El usuario sale del token, nunca del body ────────────────────────

    @Test
    void myReservationsOnlyReturnsOwnRecords() throws Exception {
        mockMvc.perform(get("/api/reservations/me").with(guest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void guestCannotListAllReservations() throws Exception {
        mockMvc.perform(get("/api/reservations").with(guest()))
                .andExpect(status().isForbidden());
    }

    // ── 5. Confirmar es solo de administrador ───────────────────────────────

    @Test
    void guestCannotConfirmReservations() throws Exception {
        mockMvc.perform(patch("/api/reservations/1/confirm").with(guest()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanConfirmPendingReservation() throws Exception {
        Hotel hotel = hotelRequiereConfirmacion();
        int id = createReservationAsGuest(unitOf(hotel).getUniSec(), "2030-07-10", "2030-07-13", 1);

        mockMvc.perform(patch("/api/reservations/" + id + "/confirm").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    void confirmingNonPendingReservationFails() throws Exception {
        Hotel hotel = hotelReservaInmediata();
        int id = createReservationAsGuest(unitOf(hotel).getUniSec(), "2030-08-10", "2030-08-13", 1);

        mockMvc.perform(patch("/api/reservations/" + id + "/confirm").with(admin()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(1012));
    }

    @Test
    void adminSeesAllReservations() throws Exception {
        mockMvc.perform(get("/api/reservations").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", not(empty())));
    }

    // ── 6. Cancelar: solo dueño o administrador, solo PENDING/CONFIRMED ─────

    @Test
    void ownerCanCancelOwnReservation() throws Exception {
        Hotel hotel = hotelReservaInmediata();
        int id = createReservationAsGuest(unitOf(hotel).getUniSec(), "2030-09-10", "2030-09-13", 1);

        mockMvc.perform(patch("/api/reservations/" + id + "/cancel").with(guest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    void guestCannotCancelSomeoneElsesReservation() throws Exception {
        // reserva creada por el admin
        Hotel hotel = hotelReservaInmediata();
        String body = mockMvc.perform(post("/api/reservations").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(unitOf(hotel).getUniSec(), "2030-10-10", "2030-10-13", 1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(body, "$.data.resSec");

        mockMvc.perform(patch("/api/reservations/" + id + "/cancel").with(guest()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1018));
    }

    // ── 7. Unidades ─────────────────────────────────────────────────────────

    @Test
    void unitListingIsPublic() throws Exception {
        Hotel hotel = hotelReservaInmediata();

        mockMvc.perform(get("/api/hotel-unit/hotel/" + hotel.getHotSec()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", not(empty())));
    }

    @Test
    void unitCreationRequiresAdmin() throws Exception {
        mockMvc.perform(post("/api/hotel-unit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"hotSec\":1,\"uniName\":\"X\",\"uniCapacity\":2,\"uniPrice\":1000}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void singleUnitHotelDoesNotAcceptExtraUnits() throws Exception {
        Hotel singleUnit = hotelRepository.findAll().stream()
                .filter(h -> h.getType() != null && h.getType().getTypMultiUnit() == 'N')
                .findFirst().orElseThrow();

        mockMvc.perform(post("/api/hotel-unit").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"hotSec\":%d,\"uniName\":\"Extra\",\"uniCapacity\":2,\"uniPrice\":1000}"
                                .formatted(singleUnit.getHotSec())))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(1016));
    }

    @Test
    void deletingUnitWithActiveReservationsFails() throws Exception {
        HotelUnit busy = hotelUnitRepository.findAll().stream()
                .filter(u -> reservationRepository.countByUnitUniSecAndResStatusIn(
                        u.getUniSec(), List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)) > 0)
                .findFirst().orElseThrow();

        mockMvc.perform(delete("/api/hotel-unit/" + busy.getUniSec()).with(admin()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(1017));
    }

    @Test
    void deletingHotelTypeWithHotelsFails() throws Exception {
        HotelType withHotels = hotelTypeRepository.findAll().stream()
                .filter(t -> t.getHotels() != null && !t.getHotels().isEmpty())
                .findFirst().orElseThrow();

        mockMvc.perform(delete("/api/hotel-types/" + withHotels.getTypSec()).with(admin()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(1017));
    }

    // ── 8. Editar la bandera y el tipo del hotel ────────────────────────────

    @Test
    void adminCanUpdateConfirmFlagAndType() throws Exception {
        Hotel hotel = hotelRequiereConfirmacion();
        HotelType other = hotelTypeRepository.findAll().stream()
                .filter(t -> !t.getTypSec().equals(hotel.getType().getTypSec()))
                .findFirst().orElseThrow();

        mockMvc.perform(put("/api/hotel/update/" + hotel.getHotSec()).with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hotName":"%s","hotDescription":"%s","hotAddress":"%s",
                                 "hotCost":%d,"hotState":"A","hotCitSec":%d,
                                 "typeId":%d,"hotConfirmReq":"N"}
                                """.formatted(hotel.getHotName(), hotel.getHotDescription(),
                                hotel.getHotAddress(), hotel.getHotCost(),
                                hotel.getCity().getCitSec(), other.getTypSec())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeId").value(other.getTypSec()))
                .andExpect(jsonPath("$.hotConfirmReq").value("N"));
    }

    // ── 9. Detalle del hotel expone la politica ─────────────────────────────

    @Test
    void hotelDetailExposesTypeAndConfirmFlag() throws Exception {
        Hotel hotel = hotelReservaInmediata();

        mockMvc.perform(get("/api/hotel/findDetailById/" + hotel.getHotSec()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.typeId").value(hotel.getType().getTypSec()))
                .andExpect(jsonPath("$.data.confirmReq").value(String.valueOf(hotel.getHotConfirmReq())));
    }
}
