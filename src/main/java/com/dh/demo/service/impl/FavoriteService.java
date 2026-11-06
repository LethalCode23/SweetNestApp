package com.dh.demo.service.impl;

import com.dh.demo.dto.response.FavoriteResponse;
import com.dh.demo.entity.Favorite;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.User;
import com.dh.demo.exception.ForbiddenOperationException;
import com.dh.demo.exception.HotelNotFoundException;
import com.dh.demo.exception.InactiveRecordException;
import com.dh.demo.mapper.FavoriteMapper;
import com.dh.demo.repository.FavoriteRepository;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.service.IFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService implements IFavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final HotelRepository hotelRepository;
    private final IUserRepository userRepository;
    private final FavoriteMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteResponse> findMine(String userEmail, Pageable pageable) {
        User user = findUser(userEmail);
        return favoriteRepository
                .findByUserUserSecOrderByFavCreatedAtDescFavSecDesc(user.getUserSec(), pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> findMyHotelIds(String userEmail) {
        return favoriteRepository.findHotelIdsByUser(findUser(userEmail).getUserSec());
    }

    @Override
    @Transactional
    public FavoriteResponse add(Integer hotSec, String userEmail) {
        User user = findUser(userEmail);

        // Ya guardado: se devuelve tal cual (no se valida estado, no se duplica).
        var existing = favoriteRepository.findByUserUserSecAndHotelHotSec(user.getUserSec(), hotSec);
        if (existing.isPresent()) {
            return mapper.toDto(existing.get());
        }

        Hotel hotel = hotelRepository.findById(hotSec)
                .orElseThrow(() -> new HotelNotFoundException("Hotel no encontrado con id: " + hotSec));
        if (hotel.getHotState() == null || hotel.getHotState() != 'A') {
            throw new InactiveRecordException("El hotel no esta activo");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setHotel(hotel);
        return mapper.toDto(favoriteRepository.save(favorite));
    }

    @Override
    @Transactional
    public void remove(Integer hotSec, String userEmail) {
        User user = findUser(userEmail);
        favoriteRepository.findByUserUserSecAndHotelHotSec(user.getUserSec(), hotSec)
                .ifPresent(favoriteRepository::delete);
    }

    private User findUser(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ForbiddenOperationException("No se pudo identificar al usuario"));
    }
}
