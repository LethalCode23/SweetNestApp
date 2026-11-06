package com.dh.demo.service;

import com.dh.demo.dto.response.FavoriteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IFavoriteService {

    Page<FavoriteResponse> findMine(String userEmail, Pageable pageable);

    List<Integer> findMyHotelIds(String userEmail);

    /** Idempotente: si ya era favorito devuelve el existente. */
    FavoriteResponse add(Integer hotSec, String userEmail);

    /** Idempotente: si no era favorito no hace nada. */
    void remove(Integer hotSec, String userEmail);
}
