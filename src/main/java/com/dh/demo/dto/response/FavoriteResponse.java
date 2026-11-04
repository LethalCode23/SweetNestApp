package com.dh.demo.dto.response;

import com.dh.demo.dto.HotelDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/** Favorito del usuario. El hotel viene en el mismo formato que las cards de busqueda. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {

    private Integer favSec;
    private LocalDateTime createdAt;
    private HotelDto hotel;
}
