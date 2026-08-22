package com.dh.demo.service.impl;

import com.dh.demo.dto.ProfileDto;
import com.dh.demo.entity.Profile;
import com.dh.demo.repository.IProfileRepository;
import com.dh.demo.service.IProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
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
    public ProfileDto save(ProfileDto profileDto) {

        Profile profile = mapToEntity(profileDto);
        Profile saved = profileRepository.save(profile);

        return mapToDto(saved);
    }

    @Override
    public ProfileDto update(Long id, ProfileDto profileDto) {

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + id));

        profile.setName(profileDto.getProName());
        profile.setState(profileDto.getProState());

        Profile updated = profileRepository.save(profile);

        return mapToDto(updated);
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
        profile.setName(dto.getProName());
        profile.setState(dto.getProState());

        return profile;
    }

    private ProfileDto mapToDto(Profile profile) {

        ProfileDto dto = new ProfileDto();
        dto.setProId(profile.getId());
        dto.setProName(profile.getName());
        dto.setProState(profile.getState());
        dto.setIsDefault(profile.getIsDefault());

        return dto;
    }
}