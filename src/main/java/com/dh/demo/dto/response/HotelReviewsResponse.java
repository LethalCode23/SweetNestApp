package com.dh.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/** Resumen de valoraciones de un hotel + una pagina de resenas (mas recientes primero). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelReviewsResponse {

    /** Promedio redondeado a 1 decimal; 0 si no hay valoraciones. */
    private double ratingAvg;
    private int ratingCount;
    /** Cantidad por estrellas, de 5 a 1: [cinco, cuatro, tres, dos, una]. */
    private List<Long> distribution;
    private Page<ReviewResponse> reviews;
}
