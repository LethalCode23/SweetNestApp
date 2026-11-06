package com.dh.demo.service.impl;

import com.dh.demo.dto.*;
import com.dh.demo.entity.Category;
import com.dh.demo.entity.City;
import com.dh.demo.entity.Feature;
import com.dh.demo.entity.Hotel;
import com.dh.demo.entity.HotelType;
import com.dh.demo.entity.User;
import com.dh.demo.exception.HotelNotFoundException;
import com.dh.demo.exception.HotelTypeNotFoundException;
import com.dh.demo.exception.UserNotHostException;
import com.dh.demo.exception.UserNotFoundException;
import com.dh.demo.mapper.FeatureMapper;
import com.dh.demo.mapper.HotelMapper;
import com.dh.demo.repository.CategoryRepository;
import com.dh.demo.repository.CityRepository;
import com.dh.demo.repository.HotelRepository;
import com.dh.demo.repository.HotelTypeRepository;
import com.dh.demo.repository.IUserRepository;
import com.dh.demo.service.IHotelService;
import com.dh.demo.specification.HotelSpecification;
import lombok.AllArgsConstructor;
import com.dh.demo.dto.response.SearchSuggestionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HotelService implements IHotelService {

    private final HotelRepository repository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final HotelTypeRepository hotelTypeRepository;
    private final IUserRepository userRepository;
    private final FeatureService featureService;
    private final FeatureMapper featureMapper;
    private final HotelMapper hotelMapper;

    @Override
    @Transactional
    public HotelDto save(HotelDto hotelDto) {

        Hotel hotel = new Hotel();

        hotel.setHotName(hotelDto.getHotName());
        hotel.setHotDescription(hotelDto.getHotDescription());
        hotel.setHotAddress(hotelDto.getHotAddress());
        hotel.setHotCost(hotelDto.getHotCost());
        hotel.setHotState(hotelDto.getHotState());
        hotel.setHotConfirmReq(hotelDto.getHotConfirmReq() == null ? 'S' : hotelDto.getHotConfirmReq());

        City city = cityRepository.findById(hotelDto.getHotCitSec())
                .orElseThrow(() -> new RuntimeException("City not found"));

        hotel.setType(findType(hotelDto.getTypeId()));
        hotel.setHost(findHost(hotelDto.getHostId()));

        if (hotelDto.getCategoryIds() != null && !hotelDto.getCategoryIds().isEmpty()) {

            List<Category> categories = categoryRepository.findAllById(hotelDto.getCategoryIds());
            hotel.setCategories(categories);
        }

        if (hotelDto.getFeatureIds() != null && !hotelDto.getFeatureIds().isEmpty()) {

            List<FeatureDto> featuresDto = featureService.findByAllId(hotelDto.getFeatureIds());

            List<Feature> features = featuresDto
                    .stream()
                    .map(featureMapper::toEntity)
                    .collect(Collectors.toList());

            hotel.setFeatures(features);
        }

        hotel.setCity(city);
        Hotel savedHotel = repository.save(hotel);

        return mapToDto(savedHotel);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<HotelDto> findById(Integer id) {

        return repository.findById(id).map(this::mapToDto);
    }

    @Override
    @Transactional
    public HotelDto update(Integer id, HotelDto hotelDto) {

        Hotel hotel = repository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel no found with id: " + id));

        City city = cityRepository.findById(hotelDto.getHotCitSec())
                .orElseThrow(() -> new RuntimeException("City no found"));

        hotel.setHotName(hotelDto.getHotName());
        hotel.setHotDescription(hotelDto.getHotDescription());
        hotel.setHotAddress(hotelDto.getHotAddress());
        hotel.setHotCost(hotelDto.getHotCost());
        hotel.setCity(city);
        hotel.setHotState(hotelDto.getHotState());

        if (hotelDto.getTypeId() != null) {
            hotel.setType(findType(hotelDto.getTypeId()));
        }
        if (hotelDto.getHotConfirmReq() != null) {
            hotel.setHotConfirmReq(hotelDto.getHotConfirmReq());
        }
        // Fase 2: hostId null = no cambia el anfitrion actual.
        if (hotelDto.getHostId() != null) {
            hotel.setHost(findHost(hotelDto.getHostId()));
        }

        if (hotelDto.getCategoryIds() != null && !hotelDto.getCategoryIds().isEmpty()) {

            List<Category> categories = categoryRepository.findAllById(hotelDto.getCategoryIds());
            hotel.setCategories(categories);
        }

        if (hotelDto.getFeatureIds() != null && !hotelDto.getFeatureIds().isEmpty()) {

            List<FeatureDto> featuresDto = featureService.findByAllId(hotelDto.getFeatureIds());

            List<Feature> features = featuresDto
                    .stream()
                    .map(featureMapper::toEntity)
                    .collect(Collectors.toList());

            hotel.setFeatures(features);
        }

        Hotel updated = repository.save(hotel);

        return mapToDto(updated);
    }

    private HotelType findType(Integer typeId) {

        if (typeId == null) {
            throw new HotelTypeNotFoundException("El tipo de alojamiento es obligatorio");
        }

        return hotelTypeRepository.findById(typeId)
                .orElseThrow(() -> new HotelTypeNotFoundException("Tipo de alojamiento no encontrado con id: " + typeId));
    }

    /**
     * Anfitrion de un hotel (Fase 2): hostId null = sin anfitrion.
     * El usuario debe existir (404 / 1004) y estar marcado como anfitrion
     * con userHost='S' (409 / 1020), si no no puede ser host.
     */
    private User findHost(Long hostId) {

        if (hostId == null) {
            return null;
        }

        User user = userRepository.findById(hostId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + hostId));

        if (user.getUserHost() == null || user.getUserHost() != 'S') {
            throw new UserNotHostException(
                    "El usuario no esta marcado como anfitrion (userHost='S'): " + hostId);
        }

        return user;
    }

    @Override
    public void delete(Integer id) {
        this.repository.deleteById(id);
    }

    @Override
    public Page<HotelDto> findAll(Pageable pageable) {

        Page<Hotel> hotels = repository.findAll(pageable);
        return hotels.map(this::mapToDto);
    }

    @Override
    public Page<HotelDto> findByHotel(HotelFilterDto hotelFilterDto, Pageable pageable) {

        Specification<Hotel> specification = HotelSpecification.withFilters(hotelFilterDto);
        return repository.findAll(specification, pageable).map(this::mapToDto);
    }

    private HotelDto mapToDto(Hotel hotel) {
        return hotelMapper.toListDto(hotel);
    }

    @Override
    public HotelResponseDto findDetailById(Integer id) {

        Hotel hotel = repository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel no found with id: " + id));

        return hotelMapper.toDetailDto(hotel);
    }

    private static final int SUGGESTIONS_LIMIT = 10;
    private static final int SUGGESTIONS_MIN_LENGTH = 2;

    @Override
    @Transactional(readOnly = true)
    public List<SearchSuggestionDto> getSuggestions(String q) {

        if (q == null || q.trim().length() < SUGGESTIONS_MIN_LENGTH) {
            return List.of();
        }
        String term = q.trim();

        // Ciudades primero (es lo que filtra el buscador), luego hoteles hasta completar el tope.
        List<SearchSuggestionDto> result = new ArrayList<>(
                cityRepository.findSuggestions(term, PageRequest.of(0, SUGGESTIONS_LIMIT)));

        int remaining = SUGGESTIONS_LIMIT - result.size();
        if (remaining > 0) {
            result.addAll(repository.findSuggestions(term, PageRequest.of(0, remaining)));
        }
        return result;
    }
}