package com.dh.demo.service.impl;

import com.dh.demo.dto.request.CreateReservationRequest;
import com.dh.demo.dto.response.HotelUnavailableDatesResponse;
import com.dh.demo.dto.response.ReservationResponse;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelUnit;
import com.dh.demo.entity.Reservation;
import com.dh.demo.entity.User;
import com.dh.demo.entity.enums.ReservationStatus;
import com.dh.demo.exception.ForbiddenOperationException;
import com.dh.demo.exception.HostNotAssignedException;
import com.dh.demo.exception.HotelNotFoundException;
import com.dh.demo.exception.GuestsExceedCapacityException;
import com.dh.demo.exception.InactiveRecordException;
import com.dh.demo.exception.InvalidDateRangeException;
import com.dh.demo.exception.InvalidStateTransitionException;
import com.dh.demo.exception.ReservationInPastException;
import com.dh.demo.exception.ReservationNotFoundException;
import com.dh.demo.exception.UnitNotFoundException;
import com.dh.demo.mapper.ReservationMapper;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.HotelUnitRepository;
import com.dh.demo.repository.ReservationRepository;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.service.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    /** Estados que ocupan la unidad (regla 4). */
    private static final List<ReservationStatus> OCCUPYING =
            List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
    private static final int DEFAULT_CALENDAR_MONTHS = 6;
    private static final int MAX_CALENDAR_DAYS = 366;

    private final ReservationRepository reservationRepository;
    private final HotelUnitRepository hotelUnitRepository;
    private final HotelRepository hotelRepository;
    private final IUserRepository userRepository;
    private final ReservationMapper mapper;

    @Override
    @Transactional
    public ReservationResponse create(CreateReservationRequest request, String userEmail) {

        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ForbiddenOperationException("No se pudo identificar al usuario"));

        // ── Regla 5: la unidad y su hotel deben estar activos ──
        HotelUnit unit = hotelUnitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new UnitNotFoundException(
                        "Unidad no encontrada con id: " + request.getUnitId()));

        Hotel hotel = unit.getHotel();

        if (unit.getUniState() == null || unit.getUniState() != 'A') {
            throw new InactiveRecordException("La unidad no esta disponible");
        }
        if (hotel.getHotState() == null || hotel.getHotState() != 'A') {
            throw new InactiveRecordException("El alojamiento no esta disponible");
        }

        // ── Reglas 2 y 3: coherencia de fechas ──
        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();

        if (checkOut == null || checkIn == null || !checkOut.isAfter(checkIn)) {
            throw new InvalidDateRangeException("La fecha de salida debe ser posterior a la de entrada");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new ReservationInPastException("La fecha de entrada no puede estar en el pasado");
        }

        // ── Regla 4: huespedes <= capacidad ──
        if (request.getGuests() == null || request.getGuests() > unit.getUniCapacity()) {
            throw new GuestsExceedCapacityException(
                    "La capacidad maxima de la unidad es " + unit.getUniCapacity() + " huespedes");
        }

        // ── Reglas 7 y 8: total calculado en backend y precio congelado ──
        int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        int pricePerNight = unit.getUniPrice();

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setUnit(unit);
        reservation.setResCheckIn(checkIn);
        reservation.setResCheckOut(checkOut);
        reservation.setResGuests(request.getGuests());
        reservation.setResPrice(pricePerNight);
        reservation.setResTotal(nights * pricePerNight);

        // ── Politica del hotel: 'N' inmediata, 'S' requiere confirmacion del admin ──
        boolean requiresConfirmation = hotel.getHotConfirmReq() != null && hotel.getHotConfirmReq() == 'S';
        reservation.setResStatus(requiresConfirmation
                ? ReservationStatus.PENDING
                : ReservationStatus.CONFIRMED);

        return mapper.toDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> findMine(String userEmail, ReservationStatus status, Pageable pageable) {

        User user = findUser(userEmail);
        Long userId = user.getUserSec();

        Page<Reservation> page = (status == null)
                ? reservationRepository.findByUserUserSecOrderByResCreatedAtDesc(userId, pageable)
                : reservationRepository.findByUserUserSecAndResStatusOrderByResCreatedAtDesc(userId, status, pageable);

        return page.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> findIncoming(String userEmail, ReservationStatus status, Pageable pageable) {

        User user = findUser(userEmail);
        Long hostSec = user.getUserSec();

        // Reservas de los hoteles donde este usuario es anfitrion (hotUserSec).
        // Si no es anfitrion de nada, la query devuelve pagina vacia (200).
        Page<Reservation> page = (status == null)
                ? reservationRepository.findByUnitHotelHostUserSecOrderByResCreatedAtDesc(hostSec, pageable)
                : reservationRepository.findByUnitHotelHostUserSecAndResStatusOrderByResCreatedAtDesc(
                        hostSec, status, pageable);

        return page.map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> findAll(ReservationStatus status, Pageable pageable) {

        Page<Reservation> page = (status == null)
                ? reservationRepository.findAllByOrderByResCreatedAtDesc(pageable)
                : reservationRepository.findByResStatusOrderByResCreatedAtDesc(status, pageable);

        return page.map(mapper::toDto);
    }

    @Override
    @Transactional
    public ReservationResponse confirm(Integer id, String userEmail) {

        User user = findUser(userEmail);
        Reservation reservation = getEntity(id);
        Hotel hotel = reservation.getUnit().getHotel();

        // Puede confirmar un administrador o el anfitrion del hotel de la reserva.
        // Hotel sin anfitrion: solo admin (409 / 1019). Otro usuario: 403 / 1018.
        if (!isAdmin(user)) {

            if (hotel.getHost() == null) {
                throw new HostNotAssignedException(
                        "El hotel de esta reserva no tiene anfitrion asignado");
            }

            if (!hotel.getHost().getUserSec().equals(user.getUserSec())) {
                throw new ForbiddenOperationException(
                        "Solo el anfitrion del hotel o un administrador puede confirmar reservas");
            }
        }

        if (reservation.getResStatus() != ReservationStatus.PENDING) {
            throw new InvalidStateTransitionException(
                    "Solo se pueden confirmar reservas con estado PENDING, actual: "
                            + reservation.getResStatus());
        }

        reservation.setResStatus(ReservationStatus.CONFIRMED);

        return mapper.toDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse cancel(Integer id, String userEmail) {

        User user = findUser(userEmail);
        Reservation reservation = getEntity(id);

        boolean isOwner = reservation.getUser().getUserSec().equals(user.getUserSec());

        if (!isOwner && !isAdmin(user)) {
            throw new ForbiddenOperationException("Solo el dueño de la reserva o un administrador puede cancelarla");
        }

        ReservationStatus current = reservation.getResStatus();

        if (current != ReservationStatus.PENDING && current != ReservationStatus.CONFIRMED) {
            throw new InvalidStateTransitionException(
                    "Solo se pueden cancelar reservas PENDING o CONFIRMED, actual: " + current);
        }

        reservation.setResStatus(ReservationStatus.CANCELLED);

        return mapper.toDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public HotelUnavailableDatesResponse findUnavailableDates(Integer hotSec, LocalDate from, LocalDate to) {

        if (!hotelRepository.existsById(hotSec)) {
            throw new HotelNotFoundException("Hotel no encontrado con id: " + hotSec);
        }

        LocalDate start = (from != null) ? from : LocalDate.now();
        LocalDate end = (to != null) ? to : start.plusMonths(DEFAULT_CALENDAR_MONTHS);

        if (!end.isAfter(start)) {
            throw new InvalidDateRangeException("La fecha 'to' debe ser posterior a 'from'");
        }
        if (ChronoUnit.DAYS.between(start, end) > MAX_CALENDAR_DAYS) {
            throw new InvalidDateRangeException(
                    "El rango maximo de consulta es de " + MAX_CALENDAR_DAYS + " dias");
        }

        int totalUnits = hotelUnitRepository
                .findByHotelHotSecAndUniStateOrderByUniNameAsc(hotSec, 'A')
                .size();

        List<LocalDate> unavailable = new ArrayList<>();

        if (totalUnits > 0) {
            // dia -> unidades ocupadas ese dia (el dia de salida queda libre)
            Map<LocalDate, Set<Integer>> busyUnitsByDay = new TreeMap<>();

            for (Object[] row : reservationRepository.findActiveRangesInHotel(hotSec, start, end, OCCUPYING)) {
                Integer uniSec = (Integer) row[0];
                LocalDate checkIn = (LocalDate) row[1];
                LocalDate checkOut = (LocalDate) row[2];

                LocalDate day = checkIn.isAfter(start) ? checkIn : start;
                LocalDate last = checkOut.isBefore(end) ? checkOut : end;

                for (; day.isBefore(last); day = day.plusDays(1)) {
                    busyUnitsByDay.computeIfAbsent(day, d -> new HashSet<>()).add(uniSec);
                }
            }

            busyUnitsByDay.forEach((day, units) -> {
                if (units.size() >= totalUnits) {
                    unavailable.add(day);
                }
            });
        }

        return HotelUnavailableDatesResponse.builder()
                .hotSec(hotSec)
                .from(start)
                .to(end)
                .totalUnits(totalUnits)
                .unavailableDates(unavailable)
                .build();
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private User findUser(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ForbiddenOperationException("No se pudo identificar al usuario"));
    }

    private boolean isAdmin(User user) {
        return user.getProfile() != null && 'S' == user.getProfile().getHasControlAccess();
    }

    private Reservation getEntity(Integer id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva no encontrada con id: " + id));
    }
}
