package com.dh.demo.mapper;

import com.dh.demo.dto.response.FavoriteResponse;
import com.dh.demo.entity.Favorite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteMapper {

    private final HotelMapper hotelMapper;

    public FavoriteResponse toDto(Favorite entity) {
        return FavoriteResponse.builder()
                .favSec(entity.getFavSec())
                .createdAt(entity.getFavCreatedAt())
                .hotel(hotelMapper.toListDto(entity.getHotel()))
                .build();
    }
}
