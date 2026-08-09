package com.dh.demo.service.impl;

import com.dh.demo.dto.ProfileDto;
import com.dh.demo.entity.Profile;
import com.dh.demo.repository.ProfileRepository;
import com.dh.demo.service.IProfileService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileService implements IProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
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
        return null;
    }

    @Override
    public Optional<ProfileDto> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {

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

        return dto;
    }
}