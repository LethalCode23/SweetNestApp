package com.dh.demo.service.impl;

import com.dh.demo.dto.HotelUnitDto;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelUnit;
import com.dh.demo.entity.enums.ReservationStatus;
import com.dh.demo.exception.DependentRecordsExistException;
import com.dh.demo.exception.HotelNotFoundException;
import com.dh.demo.exception.InactiveRecordException;
import com.dh.demo.exception.InvalidDateRangeException;
import com.dh.demo.exception.ReservationInPastException;
import com.dh.demo.exception.UnitNotFoundException;
import com.dh.demo.mapper.HotelUnitMapper;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.HotelUnitRepository;
import com.dh.demo.repository.ReservationRepository;
import com.dh.demo.service.IHotelUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HotelUnitService implements IHotelUnitService {

    /** Estados que ocupan la unidad. */
    private static final List<ReservationStatus> OCCUPYING =
            List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

    private final HotelUnitRepository hotelUnitRepository;
    private final HotelRepository hotelRepository;
    private final ReservationRepository reservationRepository;
    private final HotelUnitMapper mapper;

    @Override
    @Transactional
    public HotelUnitDto create(HotelUnitDto dto) {

        Hotel hotel = hotelRepository.findById(dto.getHotSec())
                .orElseThrow(() -> new HotelNotFoundException("Hotel no encontrado con id: " + dto.getHotSec()));

        // Los hoteles de una sola unidad ya tienen su unidad automatica.
        if (hotel.getType() != null && 'N' == hotel.getType().getTypMultiUnit()) {
            throw new InactiveRecordException(
                    "El hotel es de tipo de una sola unidad: no se pueden crear unidades adicionales");
        }

        return mapper.toDto(hotelUnitRepository.save(mapper.toEntity(dto, hotel)));
    }

    @Override
    @Transactional
    public HotelUnitDto update(Integer id, HotelUnitDto dto) {

        HotelUnit entity = getEntity(id);
        mapper.updateEntity(dto, entity);

        return mapper.toDto(hotelUnitRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public HotelUnitDto findById(Integer id) {
        return mapper.toDto(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelUnitDto> findByHotel(Integer hotSec) {

        if (!hotelRepository.existsById(hotSec)) {
            throw new HotelNotFoundException("Hotel no encontrado con id: " + hotSec);
        }

        return hotelUnitRepository.findByHotelHotSecAndUniStateOrderByUniNameAsc(hotSec, 'A')
                .stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelUnitDto> findAvailable(Integer hotSec, LocalDate checkIn, LocalDate checkOut) {

        if (!hotelRepository.existsById(hotSec)) {
            throw new HotelNotFoundException("Hotel no encontrado con id: " + hotSec);
        }
        // Mismas reglas de fechas que al crear la reserva (ReservationService.create)
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new InvalidDateRangeException("La fecha de salida debe ser posterior a la de entrada");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new ReservationInPastException("La fecha de entrada no puede estar en el pasado");
        }

        Set<Integer> busy = new HashSet<>(
                reservationRepository.findBusyUnitIdsInHotel(hotSec, checkIn, checkOut, OCCUPYING));

        return hotelUnitRepository.findByHotelHotSecAndUniStateOrderByUniNameAsc(hotSec, 'A')
                .stream()
                .filter(u -> !busy.contains(u.getUniSec()))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Integer id) {

        HotelUnit entity = getEntity(id);

        if (reservationRepository.countByUnitUniSecAndResStatusIn(id, OCCUPYING) > 0) {
            throw new DependentRecordsExistException(
                    "No se puede eliminar la unidad: tiene reservas activas");
        }

        hotelUnitRepository.delete(entity);
    }

    private HotelUnit getEntity(Integer id) {
        return hotelUnitRepository.findById(id)
                .orElseThrow(() -> new UnitNotFoundException("Unidad no encontrada con id: " + id));
    }
}
