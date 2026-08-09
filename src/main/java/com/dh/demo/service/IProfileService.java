package com.dh.demo.service;

import com.dh.demo.dto.ProfileDto;
import java.util.Optional;

public interface IProfileService {

    ProfileDto save(ProfileDto profileDto);

    ProfileDto update(Long id, ProfileDto profileDto);

    Optional<ProfileDto> findById(Long id);

    void delete(Long id);
}