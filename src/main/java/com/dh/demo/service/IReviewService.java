package com.dh.demo.service;

import com.dh.demo.dto.request.ReviewRequest;
import com.dh.demo.dto.response.HotelReviewsResponse;
import com.dh.demo.dto.response.MyReviewResponse;
import com.dh.demo.entity.Hotel;
import org.springframework.data.domain.Pageable;

public interface IReviewService {

    /** Publico: resumen (promedio, cantidad, distribucion) + pagina de resenas. */
    HotelReviewsResponse findByHotel(Integer hotSec, Pageable pageable);

    /** Si el usuario del token puede valorar el hotel y su resena actual. */
    MyReviewResponse findMine(Integer hotSec, String userEmail);

    /** Crea o actualiza la valoracion del usuario y devuelve el resumen ya recalculado. */
    HotelReviewsResponse upsert(Integer hotSec, String userEmail, ReviewRequest request);

    /** Recalcula hotRatingAvg / hotRatingCount del hotel (lo usa tambien el DataInitializer). */
    void recalculateRating(Hotel hotel);
}
