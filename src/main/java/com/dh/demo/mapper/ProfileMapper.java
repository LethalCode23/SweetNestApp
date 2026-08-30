package com.dh.demo.mapper;

import com.dh.demo.dto.ProfileDto;
import com.dh.demo.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public Profile toEntity(ProfileDto dto) {

        Profile profile = new Profile();

        profile.setId(dto.getProId());
        profile.setName(dto.getProName());
        profile.setState(dto.getProState());
        profile.setHasControlAccess(dto.getHasControlAccess());

        return profile;
    }

    public ProfileDto toDto(Profile profile) {

        ProfileDto dto = new ProfileDto();
        dto.setProId(profile.getId());
        dto.setProName(profile.getName());
        dto.setProState(profile.getState());
        dto.setIsDefault(profile.getIsDefault());
        dto.setHasControlAccess(profile.getHasControlAccess());

        return dto;
    }
}