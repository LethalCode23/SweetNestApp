package com.dh.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Una resena publicada. userName = nombre + inicial del apellido ("Juan P."). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Integer revSec;
    private int stars;
    private String comment;
    private String userName;
    /** Fecha de publicacion (ultima edicion). */
    private LocalDateTime date;
}
