package com.dh.demo.service;

import com.dh.demo.dto.ProfileDto;
import com.dh.demo.entity.Profile;

import java.util.List;
import java.util.Optional;

public interface IProfileService {

    ProfileDto save(ProfileDto profileDto);

    ProfileDto update(Long id, ProfileDto profileDto);

    Optional<ProfileDto> findById(Long id);

    List<ProfileDto> findAll();

    void delete(Long id);
    Optional<ProfileDto> findByIsDefault(Character isDefault);
}