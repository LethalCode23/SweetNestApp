package com.dh.demo.service.impl;

import com.dh.demo.common.ApiResponseCode;
import com.dh.demo.dto.ProfileDto;
import com.dh.demo.entity.Profile;
import com.dh.demo.exception.BusinessRuleException;
import com.dh.demo.repository.IProfileRepository;
import com.dh.demo.service.IProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileService implements IProfileService {

    private final IProfileRepository profileRepository;

    public ProfileService(IProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public ProfileDto save(ProfileDto profileDto) {

        Profile profile = mapToEntity(profileDto);
        profile.setId(null); // siempre alta: el id lo genera la BD (antes llegaba 0 y JPA hacia merge)

        Profile saved = profileRepository.save(profile);
        keepSingleDefault(saved);

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ProfileDto update(Long id, ProfileDto profileDto) {

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + id));

        Character isDefault = flag(profileDto.getIsDefault(), 'N');

        // Siempre debe existir un perfil por defecto (lo usa el registro: UserService → findByIsDefault('S')).
        // Se cambia marcando otro, no desmarcando el actual.
        if (isYes(profile.getIsDefault()) && !isYes(isDefault)) {
            throw new BusinessRuleException(
                    "Debe haber un perfil por defecto. Marca otro perfil como predeterminado.",
                    ApiResponseCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        }

        profile.setName(requireName(profileDto.getProName()));
        profile.setState(flag(profileDto.getProState(), 'A'));
        profile.setIsDefault(isDefault);
        profile.setHasControlAccess(flag(profileDto.getHasControlAccess(), 'N'));

        Profile updated = profileRepository.save(profile);
        keepSingleDefault(updated);

        return mapToDto(updated);
    }

    /** Si este perfil quedo como predeterminado, desmarca a los demas (solo puede haber uno). */
    private void keepSingleDefault(Profile current) {
        if (!isYes(current.getIsDefault())) {
            return;
        }
        List<Profile> others = profileRepository.findAll().stream()
                .filter(p -> !p.getId().equals(current.getId()) && isYes(p.getIsDefault()))
                .toList();
        others.forEach(p -> p.setIsDefault('N'));
        profileRepository.saveAll(others);
    }

    private static boolean isYes(Character value) {
        return value != null && value == 'S';
    }

    private static Character flag(Character value, char fallback) {
        return value == null ? fallback : value;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("El nombre del perfil es obligatorio",
                    ApiResponseCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        }
        return name.trim();
    }

    @Override
    public Optional<ProfileDto> findById(Long id) {
        return profileRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    public List<ProfileDto> findAll() {

        return profileRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {

        if (!profileRepository.existsById(id)) {
            throw new EntityNotFoundException("Profile not found with id: " + id);
        }

        profileRepository.deleteById(id);
    }

    @Override
    public Optional<ProfileDto> findByIsDefault(Character isDefault) {

        return profileRepository.findByIsDefault(isDefault)
                .map(this::mapToDto);
    }

    private Profile mapToEntity(ProfileDto dto) {

        Profile profile = new Profile();

        profile.setId(dto.getProId());
        profile.setName(requireName(dto.getProName()));
        profile.setState(flag(dto.getProState(), 'A'));
        profile.setIsDefault(flag(dto.getIsDefault(), 'N'));
        profile.setHasControlAccess(flag(dto.getHasControlAccess(), 'N'));

        return profile;
    }

    private ProfileDto mapToDto(Profile profile) {

        ProfileDto dto = new ProfileDto();
        dto.setProId(profile.getId());
        dto.setProName(profile.getName());
        dto.setProState(profile.getState());
        dto.setIsDefault(profile.getIsDefault());
        dto.setHasControlAccess(profile.getHasControlAccess());

        return dto;
    }
}