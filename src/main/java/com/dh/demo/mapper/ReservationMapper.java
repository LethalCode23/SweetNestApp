package com.dh.demo.mapper;

import com.dh.demo.dto.response.ReservationResponse;
import com.dh.demo.entity.Reservation;
import org.springframework.stereotype.Component;
import java.time.temporal.ChronoUnit;

@Component
public class ReservationMapper {

    public ReservationResponse toDto(Reservation entity) {

        long nights = ChronoUnit.DAYS.between(entity.getResCheckIn(), entity.getResCheckOut());

        return ReservationResponse.builder()
                .resSec(entity.getResSec())
                .unitId(entity.getUnit().getUniSec())
                .unitName(entity.getUnit().getUniName())
                .hotSec(entity.getUnit().getHotel().getHotSec())
                .hotName(entity.getUnit().getHotel().getHotName())
                .userId(entity.getUser().getUserSec())
                .userEmail(entity.getUser().getUserEmail())
                .checkIn(entity.getResCheckIn())
                .checkOut(entity.getResCheckOut())
                .nights((int) nights)
                .guests(entity.getResGuests())
                .pricePerNight(entity.getResPrice())
                .totalPrice(entity.getResTotal())
                .status(entity.getResStatus())
                .createdAt(entity.getResCreatedAt())
                .build();
    }
}
