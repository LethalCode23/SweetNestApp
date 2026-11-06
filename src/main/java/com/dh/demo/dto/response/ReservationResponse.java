package com.dh.demo.dto.response;

import com.dh.demo.entity.enums.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationResponse {

    private Integer resSec;
    private Integer unitId;
    private String unitName;
    private Integer hotSec;
    private String hotName;
    private Long userId;
    private String userEmail;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer nights;
    private Integer guests;
    private Integer pricePerNight;
    private Integer totalPrice;
    private ReservationStatus status;
    private LocalDateTime createdAt;
}
